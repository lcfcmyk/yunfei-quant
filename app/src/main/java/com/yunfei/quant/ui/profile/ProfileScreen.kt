package com.yunfei.quant.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunfei.quant.ui.vm.AppViewModel

@Composable
fun ProfileScreen(
    vm: AppViewModel,
    onApiKey: () -> Unit,
    onRomGuide: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(20.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("我的", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(vm.nickname, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "服务器 ${vm.baseUrl}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("设置", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))

        SettingRow(Icons.Default.Key, "OKX API Key 管理", onApiKey)
        SettingRow(Icons.Default.BatterySaver, "后台保活设置", onRomGuide)
        SettingRow(Icons.Default.Refresh, "重播新手教程", {
            vm.markTutorialSeen()
        })
        SettingRow(Icons.Default.Lock, "账户安全（PIN / 生物识别）", {})

        Spacer(Modifier.weight(1f))
        TextButton(
            onClick = { vm.logout() },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Text("退出登录", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(20.dp))
    }
}

@Composable
private fun SettingRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(14.dp))
            Text(title, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
