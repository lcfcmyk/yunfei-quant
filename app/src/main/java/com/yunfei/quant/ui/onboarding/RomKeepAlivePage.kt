package com.yunfei.quant.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yunfei.quant.util.RomUtils
import com.yunfei.quant.util.isIgnoringBatteryOptimizations

@Composable
fun RomKeepAlivePage(onDone: () -> Unit) {
    val context = LocalContext.current
    val brand = remember { RomUtils.brand }
    val steps = remember(brand) { RomUtils.steps(brand) }

    var batteryOk by remember { mutableStateOf(isIgnoringBatteryOptimizations(context)) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val obs = LifecycleEventObserver { _, e ->
            if (e == Lifecycle.Event.ON_RESUME) {
                batteryOk = isIgnoringBatteryOptimizations(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(obs)
        onDispose { lifecycleOwner.lifecycle.removeObserver(obs) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Spacer(Modifier.height(16.dp))
        Text("让云飞量化在后台活着", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(
            "你的手机是 ${brand.display}。系统默认会清理后台应用，导致行情监控中断、预警收不到。按下面几步设置一次即可。",
            fontSize = 14.sp, lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(22.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (batteryOk) Color(0xFF5ED679).copy(alpha = .12f)
                else Color(0xFFE53935).copy(alpha = .10f)
            ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    if (batteryOk) Icons.Default.CheckCircle else Icons.Default.Warning,
                    null,
                    tint = if (batteryOk) Color(0xFF2E7D32) else Color(0xFFE53935),
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        if (batteryOk) "电池优化已关闭" else "电池优化仍开启",
                        fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                    )
                    Text(
                        if (batteryOk) "后台可以被正常唤醒" else "这会导致策略预警延迟甚至收不到",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (!batteryOk) {
                    TextButton(onClick = { RomUtils.requestIgnoreBatteryOptimization(context) }) {
                        Text("去关闭")
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        Text("${brand.display} 设置步骤", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        Spacer(Modifier.height(12.dp))

        steps.forEachIndexed { i, s ->
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                Box(
                    modifier = Modifier.size(24.dp).clip(CircleShape)
                        .background(Color(0xFF009FB5).copy(alpha = .15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "${i + 1}", color = Color(0xFF009FB5),
                        fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(s, fontSize = 14.sp, lineHeight = 22.sp, modifier = Modifier.weight(1f))
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { RomUtils.openAutoStart(context) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp),
        ) { Text("打开自启动管理") }

        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = { RomUtils.openAppDetails(context) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp),
        ) { Text("打开本应用设置页") }

        Spacer(Modifier.height(26.dp))
        TextButton(onClick = onDone, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("我已完成设置")
        }
        Spacer(Modifier.height(20.dp))
    }
}
