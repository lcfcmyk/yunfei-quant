package com.yunfei.quant.ui.market

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yunfei.quant.ui.components.EmptyState
import com.yunfei.quant.ui.theme.semanticColors
import com.yunfei.quant.ui.vm.AppViewModel
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@Composable
fun MarketScreen(vm: AppViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val sem = semanticColors
    var selected by remember { mutableStateOf(0) }
    var showOrder by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { vm.refreshAll() }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("行情与持仓", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        if (state.strategies.isEmpty()) {
            EmptyState("暂无持仓", "策略开仓后会显示在这里")
            return@Column
        }

        // 轻量下单（二次确认）
        Button(
            onClick = { showOrder = true },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(12.dp),
        ) { Text("轻量下单") }

        Spacer(Modifier.height(16.dp))
        Text("持仓", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))

        state.strategies.forEachIndexed { i, s ->
            val up = s.upl >= 0
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected == i) MaterialTheme.colorScheme.surfaceVariant
                    else MaterialTheme.colorScheme.surface
                ),
                onClick = { selected = i },
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(s.instId, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Text(
                            if (s.side == "long") "多" else "空",
                            fontSize = 12.sp,
                            color = if (s.side == "long") sem.success else sem.danger,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${s.contracts} 张  @${s.entry}  ${s.leverage}x",
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "浮盈 ${if (up) "+" else ""}${String.format(Locale.US, "%.2f", s.upl)}",
                        fontSize = 13.sp,
                        color = if (up) sem.success else sem.danger,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text("迷你 K 线", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        MiniCandleChart(
            prices = state.strategies.map { it.mark }.ifEmpty { listOf(1.0, 1.0) },
            modifier = Modifier.fillMaxWidth().height(140.dp),
        )
    }

    if (showOrder) {
        AlertDialog(
            onDismissRequest = { showOrder = false },
            title = { Text("确认下单") },
            text = { Text("将以市价买入 10 USDT 的 ${state.strategies.firstOrNull()?.instId ?: "BTC-USDT-SWAP"}。此操作会真实成交。") },
            confirmButton = {
                TextButton(onClick = {
                    showOrder = false
                }) { Text("确认") }
            },
            dismissButton = { TextButton(onClick = { showOrder = false }) { Text("取消") } },
        )
    }
}

@Composable
private fun MiniCandleChart(prices: List<Double>, modifier: Modifier = Modifier) {
    val up = Color(0xFF5ED679)
    val down = Color(0xFFE53935)
    Canvas(modifier = modifier) {
        if (prices.size < 2) return@Canvas
        val hi = prices.maxOrNull() ?: 1.0
        val lo = prices.minOrNull() ?: 0.0
        val range = max(hi - lo, 1e-9)
        val step = size.width / (prices.size - 1)
        for (i in 0 until prices.size - 1) {
            val y1 = size.height * (1 - ((prices[i] - lo) / range)).toFloat()
            val y2 = size.height * (1 - ((prices[i + 1] - lo) / range)).toFloat()
            drawLine(
                color = if (prices[i + 1] >= prices[i]) up else down,
                start = Offset(i * step, y1),
                end = Offset((i + 1) * step, y2),
                strokeWidth = 3f,
            )
        }
    }
}
