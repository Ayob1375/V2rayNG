package com.v2ray.ang.ui.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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
import com.v2ray.ang.ui.compose.colorPingGood
import com.v2ray.ang.ui.compose.colorPingWarn
import com.v2ray.ang.ui.compose.colorPingBad
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
 * Bottom status panel: live speed, uptime, session traffic, quick actions
 * (smart connect + update all subscriptions) and the main connect FAB.
 */
@Composable
fun MainBottomBar(
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

    // Pulsing halo behind the FAB while connected.
    val pulseTransition = rememberInfiniteTransition(label = "fabPulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = { onAction(MainAction.TestCurrentServer) })
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            AppDivider()

            // Live speed row (only while the core is running).
            if (isRunning) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
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
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Box(Modifier.weight(1f))
                    }
                    Text(
                        text = traffic.proxyDownSpeed.toSpeedString(),
                        color = colorPingGood,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Text(
                        text = traffic.proxyUpSpeed.toSpeedString(),
                        color = colorPingWarn,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isRunning) 60.dp else 64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = displayText,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = if (isRunning) 1 else 3,
                        modifier = Modifier.semantics {
                            contentDescription = displayText
                        }
                    )
                    if (isRunning && traffic.sessionTotal > 0L) {
                        Text(
                            text = stringResource(
                                R.string.connection_panel_session,
                                traffic.sessionTotal.toTrafficString()
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (isRunning) {
                    Text(
                        text = stringResource(R.string.connection_panel_uptime, uptimeText),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    IconButton(onClick = { onAction(MainAction.SmartConnect) }, Modifier.size(36.dp)) {
                        Icon(
                            painterResource(R.drawable.ic_flash_on_24dp),
                            stringResource(R.string.acc_smart_connect),
                            Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { onAction(MainAction.UpdateAllSubscriptions) }, Modifier.size(36.dp)) {
                        Icon(
                            painterResource(R.drawable.ic_refresh_24dp),
                            stringResource(R.string.title_sub_update_all),
                            Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Pulsing halo behind the FAB while connected.
        if (isRunning) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 24.dp)
                    .offset(y = (-28).dp)
                    .navigationBarsPadding()
                    .size(56.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                        alpha = pulseAlpha
                    }
                    .clip(CircleShape)
                    .background(colorFabActive)
            )
        }

        FloatingActionButton(
            onClick = {
                if (!isRunning) {
                    scope.launch {
                        rotationAnim.animateTo(
                            targetValue = 720f,
                            animationSpec = tween(durationMillis = 3000)
                        )
                    }
                }
                onAction(MainAction.ToggleService)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp)
                .offset(y = (-28).dp)
                .navigationBarsPadding(),
            containerColor = if (isRunning) colorFabActive
            else if (isDarkTheme) colorFabInactiveDark
            else colorFabInactiveLight
        ) {
            Icon(
                painter = if (isRunning) painterResource(R.drawable.ic_stop_24dp)
                else painterResource(R.drawable.ic_play_24dp),
                contentDescription = stringResource(
                    if (isRunning) R.string.acc_stop else R.string.acc_start
                ),
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { rotationZ = rotationAnim.value }
            )
        }
    }
}
