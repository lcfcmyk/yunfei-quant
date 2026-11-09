package com.yunfei.quant.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.yunfei.quant.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** 品牌色（取自 logo） */
private val LogoTextColor = Color(0xFF0C2D5A)
private val SplashBg = Color(0xFFFFFFFF)

/**
 * 冷启动欢迎页
 *  阶段1  燕子从左侧飞入（1s）
 *  阶段2  扇动翅膀（scaleY 循环）
 *  阶段3  文字「云飞量化」渐显（0.5s）
 *  阶段4  停止扇动，停留 1s 后跳转
 */
@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var phase by remember { mutableIntStateOf(0) }

    val flyProgress = remember { Animatable(0f) }
    val flapTransition = rememberInfiniteTransition(label = "flap")
    val flapScale by flapTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.62f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "flapScale",
    )
    val textAlpha = remember { Animatable(0f) }
    val textOffset = remember { Animatable(24f) }

    LaunchedEffect(Unit) {
        phase = 1
        flyProgress.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
        phase = 2
        delay(500)
        phase = 3
        launch { textOffset.animateTo(0f, tween(500, easing = FastOutSlowInEasing)) }
        textAlpha.animateTo(1f, tween(500))
        phase = 4
        delay(1000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBg),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.logo_bird),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(190.dp)
                    .graphicsLayer {
                        translationX = (1f - flyProgress.value) * -900f
                        val flapping = phase == 2 || phase == 3
                        scaleY = if (flapping) flapScale else 1f
                        alpha = flyProgress.value
                    },
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "云飞量化",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = LogoTextColor,
                letterSpacing = 4.sp,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = textAlpha.value
                        translationY = textOffset.value
                    },
            )
        }
    }
}
