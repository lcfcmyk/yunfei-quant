package com.yunfei.quant.ui.onboarding

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun NotificationRationaleHost(
    visible: Boolean,
    onDismiss: () -> Unit,
    onResult: (granted: Boolean) -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted -> onResult(granted) }

    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF009FB5).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Default.NotificationsActive, null,
                    tint = Color(0xFF009FB5), modifier = Modifier.size(28.dp),
                )
            }
        },
        title = { Text("开启通知推送", fontWeight = FontWeight.Bold, fontSize = 19.sp) },
        text = {
            Column {
                Text(
                    "开启通知，策略异常和强平预警才能第一时间推给你。",
                    fontSize = 15.sp, lineHeight = 23.sp,
                )
                Spacer(Modifier.height(12.dp))
                BulletText("强平预警：保证金率逼近红线时立即提醒")
                BulletText("策略异常：进程掉线、下单失败实时告警")
                Spacer(Modifier.height(8.dp))
                Text(
                    "App 只读取服务端推送内容，不会上传你的私人信息。",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else onResult(true)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0C2D5A)),
            ) { Text("去开启") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("以后再说") } },
    )
}

@Composable
private fun BulletText(text: String) {
    Row(modifier = Modifier.padding(vertical = 3.dp)) {
        Text("· ", color = Color(0xFF009FB5), fontSize = 15.sp)
        Text(text, fontSize = 14.sp, lineHeight = 21.sp)
    }
}

fun hasNotificationPermission(context: Context): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
    } else true
