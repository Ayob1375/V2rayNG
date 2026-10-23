package com.v2ray.ang.handler

import com.v2ray.ang.AppConfig
import com.v2ray.ang.core.CoreServiceManager
import com.v2ray.ang.util.LogUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Lightweight traffic monitor that publishes live proxy/direct speeds,
 * session totals and the connection start time to the UI layer.
 *
 * The loop runs while the core is running, independent of the speed
 * notification preference, so the main screen always has fresh data.
 */
object TrafficStatsMonitor {

    /** Immutable snapshot of live traffic data consumed by the UI. */
    data class Snapshot(
        val proxyUpSpeed: Long = 0L,
        val proxyDownSpeed: Long = 0L,
        val directUpSpeed: Long = 0L,
        val directDownSpeed: Long = 0L,
        val sessionUp: Long = 0L,
        val sessionDown: Long = 0L,
        val connectedAtMillis: Long = 0L,
    ) {
        val isConnected: Boolean get() = connectedAtMillis > 0L
        val sessionTotal: Long get() = sessionUp + sessionDown
    }

    private const val QUERY_INTERVAL_MS = 1000L

    private val _snapshot = MutableStateFlow(Snapshot())
    val snapshot: StateFlow<Snapshot> = _snapshot.asStateFlow()

    private var monitorJob: Job? = null

    // Session totals accumulated by this monitor.
    // NOTE: the Go core resets every traffic counter on each query
    // (counter.Set(0) returns the value accumulated since the previous
    // query), so query results are already per-interval deltas - no
    // baselines are kept here, which also stays correct across core
    // reloads (network handovers) that restart the counters from zero.
    private var sessionUp = 0L
    private var sessionDown = 0L

    private var connectedAt = 0L

    /**
     * Starts monitoring. Called right after the core loop starts successfully.
     * Safe to call multiple times; the first call wins until [stop] is invoked.
     */
    fun start() {
        if (monitorJob?.isActive == true) return
        resetCounters()
        connectedAt = System.currentTimeMillis()
        _snapshot.value = Snapshot(connectedAtMillis = connectedAt)

        monitorJob = CoroutineScope(Dispatchers.IO).launch {
            var lastQueryTime = System.currentTimeMillis()
            while (isActive) {
                delay(QUERY_INTERVAL_MS)

                val queryTime = System.currentTimeMillis()
                val sinceLastQueryInSeconds = ((queryTime - lastQueryTime) / 1000.0)
                    .coerceAtLeast(0.001)
                lastQueryTime = queryTime

                if (!CoreServiceManager.isRunning()) continue

                var proxyUp = 0L
                var proxyDown = 0L
                var directUp = 0L
                var directDown = 0L

                try {
                    CoreServiceManager.queryAllOutboundTrafficStats().forEach { stat ->
                        when {
                            stat.tag == AppConfig.TAG_DIRECT -> when (stat.direction) {
                                AppConfig.UPLINK -> directUp += stat.value
                                AppConfig.DOWNLINK -> directDown += stat.value
                            }

                            // Accumulate stats for all proxy outbounds (including custom tags)
                            stat.tag != AppConfig.TAG_BLOCKED -> when (stat.direction) {
                                AppConfig.UPLINK -> proxyUp += stat.value
                                AppConfig.DOWNLINK -> proxyDown += stat.value
                            }
                        }
                    }
                } catch (e: Exception) {
                    LogUtil.e(AppConfig.TAG, "TrafficStatsMonitor: query failed", e)
                    continue
                }

                // The payload values are the bytes transferred since the previous
                // query (counters are reset on read), i.e. already the deltas.
                sessionUp += proxyUp.coerceAtLeast(0L)
                sessionDown += proxyDown.coerceAtLeast(0L)

                _snapshot.value = Snapshot(
                    proxyUpSpeed = (proxyUp / sinceLastQueryInSeconds).toLong(),
                    proxyDownSpeed = (proxyDown / sinceLastQueryInSeconds).toLong(),
                    directUpSpeed = (directUp / sinceLastQueryInSeconds).toLong(),
                    directDownSpeed = (directDown / sinceLastQueryInSeconds).toLong(),
                    sessionUp = sessionUp,
                    sessionDown = sessionDown,
                    connectedAtMillis = connectedAt,
                )
            }
        }
        LogUtil.i(AppConfig.TAG, "TrafficStatsMonitor started")
    }

    /**
     * Stops monitoring and clears the published snapshot.
     * Called when the core service stops.
     */
    fun stop() {
        monitorJob?.cancel()
        monitorJob = null
        resetCounters()
        connectedAt = 0L
        _snapshot.value = Snapshot()
        LogUtil.i(AppConfig.TAG, "TrafficStatsMonitor stopped")
    }

    private fun resetCounters() {
        sessionUp = 0L
        sessionDown = 0L
    }
}
