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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yunfei.quant.R
import com.yunfei.quant.ui.theme.BrandCyan
import com.yunfei.quant.ui.theme.BrandNavy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var phase by remember { mutableIntStateOf(0) }

    val flyProgress = remember { Animatable(0f) }
    val flapTransition = rememberInfiniteTransition(label = "flap")
    val flapScale by flapTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(280, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "flapScale",
    )
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        phase = 1
        flyProgress.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
        phase = 2
        delay(600)
        phase = 3
        textAlpha.animateTo(1f, tween(500))
        phase = 4
        delay(1000)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BrandNavy, BrandCyan.copy(alpha = 0.85f)))),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.logo_bird),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        translationX = (1f - flyProgress.value) * -900f
                        val flapping = phase == 2 || phase == 3
                        scaleY = if (flapping) flapScale else 1f
                        alpha = flyProgress.value
                    },
            )
            Spacer(Modifier.height(20.dp))
            Image(
                painter = painterResource(R.drawable.logo_text),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .height(56.dp)
                    .alpha(textAlpha.value),
            )
        }
    }
}
