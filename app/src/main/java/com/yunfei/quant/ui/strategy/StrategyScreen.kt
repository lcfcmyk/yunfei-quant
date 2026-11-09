package com.yunfei.quant.ui.strategy

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yunfei.quant.ui.components.EmptyState
import com.yunfei.quant.ui.theme.semanticColors
import com.yunfei.quant.ui.vm.AppViewModel
import java.util.Locale

@Composable
fun StrategyScreen(vm: AppViewModel) {
    val state by vm.state.collectAsStateWithLifecycle()
    val sem = semanticColors
    var filter by remember { mutableStateOf("全部") }
    val filters = listOf("全部", "运行中", "异常", "已暂停")

    LaunchedEffect(Unit) { vm.refreshAll() }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 20.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("策略", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { f ->
                FilterChip(
                    selected = filter == f,
                    onClick = { filter = f },
                    label = { Text(f, fontSize = 13.sp) },
                )
            }
        }
        Spacer(Modifier.height(14.dp))

        val shown = state.strategies.filter {
            when (filter) {
                "运行中" -> it.status == "running"
                "异常" -> it.status == "error"
                "已暂停" -> it.status == "paused"
                else -> true
            }
        }

        if (shown.isEmpty()) {
            EmptyState("没有符合条件的策略")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(shown, key = { it.id }) { s ->
                    val up = s.upl >= 0
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (s.status == "error") sem.dangerContainer
                            else MaterialTheme.colorScheme.surface
                        ),
                    ) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(s.instId, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                Switch(
                                    checked = s.status == "running",
                                    onCheckedChange = { vm.toggleStrategy(s.id, it) },
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "${if (s.side == "long") "多" else "空"} ${s.contracts} 张  " +
                                        "开仓 ${s.entry}  现价 ${s.mark}  ${s.leverage}x",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "浮动盈亏 ${if (up) "+" else ""}${String.format(Locale.US, "%.2f", s.upl)} USDT",
                                fontSize = 13.sp, fontWeight = FontWeight.Medium,
                                color = if (up) sem.success else sem.danger,
                            )
                        }
                    }
                }
            }
        }
    }
}
