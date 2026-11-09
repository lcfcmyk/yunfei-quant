package com.yunfei.quant.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hapticfeedback.HapticFeedbackType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yunfei.quant.data.StrategyItem
import com.yunfei.quant.ui.components.EmptyState
import com.yunfei.quant.ui.components.StatusDot
import com.yunfei.quant.ui.onboarding.CoachMarkOverlay
import com.yunfei.quant.ui.onboarding.CoachStep
import com.yunfei.quant.ui.theme.semanticColors
import com.yunfei.quant.ui.vm.AppViewModel
import com.yunfei.quant.util.rememberNotificationBlocked
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    vm: AppViewModel,
    showTutorial: Boolean,
    onTutorialDone: () -> Unit,
    onOpenRomGuide: () -> Unit,
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val semantic = semanticColors
    val haptic = LocalHapticFeedback.current
    val notifBlocked by rememberNotificationBlocked()

    var confirmPause by remember { mutableStateOf(false) }
    var pausing by remember { mutableStateOf(false) }
    var pausedOk by remember { mutableStateOf(false) }

    var equityRect by remember { mutableStateOf(Rect.Zero) }
    var strategyRect by remember { mutableStateOf(Rect.Zero) }
    var showCoach by remember { mutableStateOf(showTutorial) }

    LaunchedEffect(Unit) { vm.refreshAll() }

    val isWide = LocalConfiguration.current.screenWidthDp >= 600

    Box(modifier = Modifier.fillMaxSize().statusBarsPadding()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ---- 通知被拦截横幅 ----
            if (notifBlocked) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(semantic.dangerContainer)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Default.NotificationsOff, null,
                        tint = semantic.danger, modifier = Modifier.width(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "通知被系统拦截，强平预警收不到",
                        fontSize = 13.sp, color = semantic.danger,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onOpenRomGuide) { Text("去设置", fontSize = 13.sp) }
                }
            }

            // ---- 顶部 ----
            Row(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text("Hi, ${vm.nickname}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(
                        SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(Date()),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusDot(if (state.overview != null) semantic.success else semantic.warning)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (state.overview != null) "已连接" else "未连接",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (isWide) {
                // ---- Expanded：左右双列 ----
                Row(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
                    Column(
                        modifier = Modifier.weight(0.4f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                    ) {
                        EquityCard(state.overview?.equity ?: 0.0,
                            state.overview?.todayPnl ?: 0.0,
                            state.overview?.todayPnlPct ?: 0.0,
                            state.overview?.availableMargin ?: 0.0,
                            modifier = Modifier.onGloballyPositioned {
                                equityRect = it.boundsInWindow()
                            })
                        RiskCard(state.overview?.marginRatio ?: 0.0,
                            state.overview?.liqPrice)
                        PauseAllButton(
                            onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); confirmPause = true },
                            enabled = state.strategies.isNotEmpty(),
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(0.6f)) {
                        StrategyList(
                            items = state.strategies,
                            onGloballyPositioned = { strategyRect = it },
                            onToggle = { id, run -> vm.toggleStrategy(id, run) },
                        )
                    }
                }
            } else {
                // ---- Compact：单列 ----
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                ) {
                    EquityCard(
                        state.overview?.equity ?: 0.0,
                        state.overview?.todayPnl ?: 0.0,
                        state.overview?.todayPnlPct ?: 0.0,
                        state.overview?.availableMargin ?: 0.0,
                        modifier = Modifier.onGloballyPositioned {
                            equityRect = it.boundsInWindow()
                        },
                    )
                    Spacer(Modifier.height(14.dp))
                    RiskCard(state.overview?.marginRatio ?: 0.0, state.overview?.liqPrice)
                    Spacer(Modifier.height(14.dp))
                    PauseAllButton(
                        onClick = { haptic.performHapticFeedback(HapticFeedbackType.LongPress); confirmPause = true },
                        enabled = state.strategies.isNotEmpty(),
                    )
                    Spacer(Modifier.height(18.dp))
                    Text("运行中策略", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .onGloballyPositioned { strategyRect = it.boundsInWindow() },
                    ) {
                        StrategyList(items = state.strategies, onToggle = { id, run ->
                            vm.toggleStrategy(id, run)
                        })
                    }
                }
            }
        }

        // ---- 新手教程 ----
        if (showCoach && equityRect != Rect.Zero && strategyRect != Rect.Zero) {
            CoachMarkOverlay(
                steps = listOf(
                    CoachStep(
                        target = equityRect,
                        title = "总权益与今日盈亏",
                        description = "这里是你账户的实时净值。数字变绿代表今天赚钱，变红代表回撤。",
                        arrowUp = true,
                    ),
                    CoachStep(
                        target = strategyRect,
                        title = "运行中的策略",
                        description = "异常的策略会置顶标红。点右侧按钮可以单独暂停某个策略。",
                        arrowUp = false,
                    ),
                ),
                onFinish = {
                    showCoach = false
                    vm.markTutorialSeen()
                    onTutorialDone()
                },
            )
        }

        // ---- 暂停全部：二次确认 ----
        if (confirmPause) {
            AlertDialog(
                onDismissRequest = { if (!pausing) confirmPause = false },
                title = { Text("暂停全部策略？") },
                text = { Text("所有持仓会立刻停止自动管理，止损也将不再执行。行情剧烈波动时这样做很危险。") },
                confirmButton = {
                    Button(
                        onClick = {
                            pausing = true
                            vm.pauseAll { ok ->
                                pausing = false
                                confirmPause = false
                                pausedOk = ok
                            }
                        },
                        enabled = !pausing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                    ) {
                        if (pausing) CircularProgressIndicator(
                            modifier = Modifier.height(18.dp), strokeWidth = 2.dp,
                            color = Color.White,
                        ) else Text("确认暂停")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { confirmPause = false }, enabled = !pausing) {
                        Text("取消")
                    }
                },
            )
        }

        if (pausedOk) {
            AlertDialog(
                onDismissRequest = { pausedOk = false },
                title = { Text("已暂停") },
                text = { Text("所有策略已停止。需要恢复时，进入「策略」页逐个开启。") },
                confirmButton = {
                    TextButton(onClick = { pausedOk = false }) { Text("知道了") }
                },
            )
        }
    }
}

@Composable
private fun EquityCard(
    equity: Double,
    todayPnl: Double,
    todayPnlPct: Double,
    available: Double,
    modifier: Modifier = Modifier,
) {
    val sem = semanticColors
    val up = todayPnl >= 0
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("总权益 (USDT)", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            Text(
                String.format(Locale.US, "%,.2f", equity),
                fontSize = 32.sp, fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    (if (up) "+" else "") + String.format(Locale.US, "%,.2f", todayPnl) +
                            "  (" + String.format(Locale.US, "%.2f", todayPnlPct) + "%)",
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                    color = if (up) sem.success else sem.danger,
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "可用保证金 " + String.format(Locale.US, "%,.2f", available),
                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RiskCard(marginRatio: Double, liqPrice: Double?) {
    val sem = semanticColors
    val (color, label) = when {
        marginRatio >= 200 -> sem.success to "安全"
        marginRatio >= 100 -> sem.warning to "注意"
        else -> sem.danger to "危险"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("风险状态", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusDot(color)
                    Spacer(Modifier.width(6.dp))
                    Text(label, fontSize = 13.sp, color = color, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("维持保证金率", fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(String.format(Locale.US, "%.2f%%", marginRatio),
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = color)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("预估强平价", fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        liqPrice?.let { String.format(Locale.US, "%,.2f", it) } ?: "--",
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = color,
                    )
                }
            }
        }
    }
}

@Composable
private fun PauseAllButton(onClick: () -> Unit, enabled: Boolean) {
    val sem = semanticColors
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = sem.dangerContainer,
            contentColor = sem.danger,
        ),
    ) {
        Icon(Icons.Default.PauseCircle, null, modifier = Modifier.width(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("暂停全部策略", fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun StrategyList(
    items: List<StrategyItem>,
    onGloballyPositioned: ((Rect) -> Unit)? = null,
    onToggle: (String, Boolean) -> Unit,
) {
    if (items.isEmpty()) {
        EmptyState("暂无运行中的策略", "有信号时策略会自动开仓")
        return
    }
    val sorted = items.sortedByDescending { it.status == "error" }
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(sorted, key = { it.id }) { s ->
            StrategyRow(s, onToggle)
        }
    }
}

@Composable
private fun StrategyRow(s: StrategyItem, onToggle: (String, Boolean) -> Unit) {
    val sem = semanticColors
    val isError = s.status == "error"
    val up = s.upl >= 0
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) sem.dangerContainer
            else MaterialTheme.colorScheme.surface
        ),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isError) {
                        StatusDot(sem.danger, 7)
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(s.instId, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    if (s.source == "builtin") "OKX内置" else "外部API",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    (if (s.side == "long") "多" else "空") +
                            " ${s.contracts} 张 @${s.entry} · ${s.leverage}x",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    (if (up) "+" else "") + String.format(Locale.US, "%.2f", s.upl),
                    fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    color = if (up) sem.success else sem.danger,
                )
            }
            if (isError && s.errorMsg != null) {
                Spacer(Modifier.height(6.dp))
                Text(s.errorMsg, fontSize = 11.sp, color = sem.danger)
            }
        }
    }
}
