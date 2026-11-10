package com.yunfei.quant.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun RegisterScreen(
    vm: AppViewModel,
    onBack: () -> Unit,
    onRegistered: () -> Unit,
) {
    var account by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var err by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.Default.ArrowBack, "返回")
        }

        Spacer(Modifier.height(8.dp))
        Text("创建账号", fontSize = 28.sp, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(6.dp))
        Text("注册后自动登录，以后用账号密码进入",
            fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(Modifier.height(30.dp))

        OutlinedTextField(
            value = account, onValueChange = { account = it.trim() },
            label = { Text("账号（登录用）") },
            supportingText = { Text("至少 3 个字符，建议英文+数字", fontSize = 11.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = nickname, onValueChange = { nickname = it },
            label = { Text("昵称（首页显示）") },
            supportingText = { Text("比如「小王」，显示为 Hi, 小王", fontSize = 11.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("密码") },
            supportingText = { Text("至少 6 位", fontSize = 11.sp) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirm, onValueChange = { confirm = it },
            label = { Text("确认密码") },
            singleLine = true,
            isError = confirm.isNotEmpty() && confirm != password,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        if (confirm.isNotEmpty() && confirm != password) {
            Text("两次输入的密码不一致", fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp))
        }

        if (err != null) {
            Spacer(Modifier.height(12.dp))
            Text(err!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }

        Spacer(Modifier.height(28.dp))
        Button(
            onClick = {
                if (account.length < 3) { err = "账号至少 3 个字符"; return@Button }
                if (password.length < 6) { err = "密码至少 6 位"; return@Button }
                if (password != confirm) { err = "两次密码不一致"; return@Button }
                if (nickname.isBlank()) { err = "请填写昵称"; return@Button }
                loading = true
                err = null
                vm.register(account, nickname, password) { ok, msg ->
                    loading = false
                    if (ok) onRegistered() else err = msg
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth().height(50.dp),
        ) {
            if (loading) CircularProgressIndicator(
                modifier = Modifier.height(20.dp), strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            ) else Text("注册并登录")
        }

        Spacer(Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text("已有账号？", fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            TextButton(onClick = onBack) { Text("去登录", fontSize = 13.sp) }
        }
    }
}
