package com.yunfei.quant.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yunfei.quant.ui.theme.semanticColors
import com.yunfei.quant.ui.vm.AppViewModel

@Composable
fun ApiKeyScreen(vm: AppViewModel, forceGuide: Boolean, onDone: () -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    val sem = semanticColors
    var confirmed by remember { mutableStateOf(!forceGuide) }

    LaunchedEffect(Unit) { vm.loadApiKey() }

    if (!confirmed) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("配置 OKX API Key") },
            text = {
                Text(
                    "接下来会让你填写 OKX 的 API Key。\n\n" +
                            "权限只勾「读取 + 交易」，绝不能勾选「提币」。\n" +
                            "建议绑定 IP 白名单，只允许你的服务器访问。"
                )
            },
            confirmButton = {
                Button(onClick = { confirmed = true }) { Text("我明白了") }
            },
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(20.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("API Key 管理", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        val info = state.apiKey
        if (info == null) {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Text("尚未绑定", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "绑定后 App 才能读取账户与持仓数据。",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(info.label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Text(info.masked, fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("权限", fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        info.permissions.forEach { p ->
                            val isWithdraw = p.equals("withdraw", true)
                            Text(
                                p,
                                fontSize = 11.sp,
                                color = if (isWithdraw) Color.White else
                                    MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .background(
                                        if (isWithdraw) sem.danger
                                        else MaterialTheme.colorScheme.surfaceVariant,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp),
                            )
                        }
                    }
                    if (info.permissions.any { it.equals("withdraw", true) }) {
                        Spacer(Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(sem.dangerContainer, RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Default.Warning, null,
                                tint = sem.danger, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "该 Key 拥有提币权限，请立即去 OKX 关闭",
                                fontSize = 12.sp, color = sem.danger,
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "IP 白名单：${info.ipWhitelist ?: "未绑定（任何 IP 可用）"}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { vm.markApiBound(); onDone() },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(14.dp),
        ) { Text(if (forceGuide) "已完成配置，进入首页" else "返回") }
    }
}
