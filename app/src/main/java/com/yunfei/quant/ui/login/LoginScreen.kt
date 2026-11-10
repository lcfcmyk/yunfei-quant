package com.yunfei.quant.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunfei.quant.ui.vm.AppViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    vm: AppViewModel,
    onSuccess: () -> Unit,
    onGoRegister: () -> Unit = {},
) {
    var account by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var server by remember { mutableStateOf(vm.baseUrl) }
    var err by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    var showServer by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(60.dp))
        Text("云飞量化", fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(8.dp))
        Text("登录你的监控驾驶舱", fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = account, onValueChange = { account = it },
            label = { Text("账号") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("密码") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))
        Text(
            if (showServer) "服务器地址" else "服务器: $server（点击修改）",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
                .then(
                    Modifier.padding(0.dp)
                ),
        )
        if (showServer) {
            OutlinedTextField(
                value = server, onValueChange = { server = it },
                label = { Text("http://IP:端口") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        } else {
            Button(onClick = { showServer = true }, modifier = Modifier.height(32.dp)) {
                Text("修改服务器", fontSize = 12.sp)
            }
        }

        if (err != null) {
            Spacer(Modifier.height(12.dp))
            Text(err!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(28.dp))
        Button(
            onClick = {
                if (account.isBlank() || password.isBlank()) {
                    err = "请填写账号和密码"
                    return@Button
                }
                vm.updateBaseUrl(server)
                loading = true
                err = null
                vm.login(account, password) { ok, msg ->
                    loading = false
                    if (ok) onSuccess() else err = msg
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
        ) {
            if (loading) CircularProgressIndicator(
                modifier = Modifier.height(20.dp),
                strokeWidth = 2.dp,
            ) else Text("登录")
        }

        Spacer(Modifier.height(14.dp))
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("还没有账号？", fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onGoRegister) {
                Text("立即注册", fontSize = 13.sp)
            }
        }

        Spacer(Modifier.height(50.dp))
    }
}
