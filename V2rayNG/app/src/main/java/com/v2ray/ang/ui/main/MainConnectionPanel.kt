package com.v2ray.ang.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.v2ray.ang.R
import com.v2ray.ang.extension.toSpeedString
import com.v2ray.ang.extension.toTrafficString
import com.v2ray.ang.handler.TrafficStatsMonitor
import com.v2ray.ang.ui.compose.AppDivider
import com.v2ray.ang.ui.compose.colorFabActive
import com.v2ray.ang.ui.compose.colorFabInactiveDark
import com.v2ray.ang.ui.compose.colorFabInactiveLight
import com.v2ray.ang.ui.compose.colorPingBad
import com.v2ray.ang.ui.compose.colorPingGood
import com.v2ray.ang.ui.compose.colorPingWarn
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Formats seconds as "1d 02:03:04", "02:03:04" or "03:04". */
private fun formatUptime(totalSeconds: Long): String {
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = (totalSeconds / 3600) % 24
    val days = totalSeconds / 86400
    val hhmmss = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    return if (days > 0) "${days}d $hhmmss"
    else if (hours > 0) hhmmss
    else String.format("%02d:%02d", minutes, seconds)
}

/**
 * Compact connection panel shown directly under the top app bar, above the
 * group tabs. It carries the connect/stop button, live ping, download/upload
 * speeds, uptime and session traffic, plus quick actions (smart connect and
 * update all subscriptions).
 *
 * It replaces the old bottom bar whose floating FAB overlapped the upload
 * speed label; here everything is laid out inline so nothing is covered.
 */
@Composable
fun MainConnectionPanel(
    displayText: String,
    isRunning: Boolean,
    isDarkTheme: Boolean,
    traffic: TrafficStatsMonitor.Snapshot,
    selectedPing: Long?,
    onAction: (MainAction) -> Unit
) {
    val scope = rememberCoroutineScope()
    val rotationAnim = remember { Animatable(0f) }

    LaunchedEffect(isRunning) {
        if (!isRunning) {
            rotationAnim.snapTo(0f)
        }
    }

    // Ticker for the uptime label while connected.
    var uptimeText by remember { mutableStateOf("") }
    if (isRunning && traffic.isConnected) {
        LaunchedEffect(traffic.connectedAtMillis) {
            while (true) {
                val seconds = (System.currentTimeMillis() - traffic.connectedAtMillis) / 1000
                uptimeText = formatUptime(seconds)
                delay(1000)
            }
        }
    } else {
        uptimeText = ""
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        AppDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = { onAction(MainAction.TestCurrentServer) })
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Connect/stop button - inline circle, never overlaps the speed labels.
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (isRunning) colorFabActive
                        else if (isDarkTheme) colorFabInactiveDark
                        else colorFabInactiveLight
                    )
                    .clickable {
                        if (!isRunning) {
                            scope.launch {
                                rotationAnim.animateTo(
                                    targetValue = 720f,
                                    animationSpec = tween(durationMillis = 1500)
                                )
                            }
                        }
                        onAction(MainAction.ToggleService)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = if (isRunning) painterResource(R.drawable.ic_stop_24dp)
                    else painterResource(R.drawable.ic_play_24dp),
                    contentDescription = stringResource(
                        if (isRunning) R.string.acc_stop else R.string.acc_start
                    ),
                    tint = Color.White,
                    modifier = Modifier
                        .size(22.dp)
                        .graphicsLayer { rotationZ = rotationAnim.value }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isRunning) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (selectedPing != null && selectedPing != 0L) {
                            val pingColor = when {
                                selectedPing < 0L -> colorPingBad
                                selectedPing < 200L -> colorPingGood
                                selectedPing < 500L -> colorPingWarn
                                else -> colorPingBad
                            }
                            Text(
                                text = stringResource(R.string.server_test_delay_value, selectedPing),
                                color = pingColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                        }
                        Text(
                            text = "↓ " + traffic.proxyDownSpeed.toSpeedString(),
                            color = colorPingGood,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "↑ " + traffic.proxyUpSpeed.toSpeedString(),
                            color = colorPingWarn,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        if (uptimeText.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = uptimeText,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                        if (traffic.sessionTotal > 0L) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = stringResource(
                                    R.string.connection_panel_session,
                                    traffic.sessionTotal.toTrafficString()
                                ),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    }
                }
            }

            if (isRunning) {
                IconButton(onClick = { onAction(MainAction.SmartConnect) }, Modifier.size(32.dp)) {
                    Icon(
                        painterResource(R.drawable.ic_flash_on_24dp),
                        stringResource(R.string.acc_smart_connect),
                        Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = { onAction(MainAction.UpdateAllSubscriptions) }, Modifier.size(32.dp)) {
                    Icon(
                        painterResource(R.drawable.ic_refresh_24dp),
                        stringResource(R.string.title_sub_update_all),
                        Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        AppDivider()
    }
}
