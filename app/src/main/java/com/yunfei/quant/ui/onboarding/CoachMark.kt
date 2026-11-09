package com.yunfei.quant.ui.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

data class CoachStep(
    val target: Rect,
    val title: String,
    val description: String,
    val arrowUp: Boolean = true,
)

@Composable
fun CoachMarkOverlay(
    steps: List<CoachStep>,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (steps.isEmpty()) return

    var index by remember { mutableIntStateOf(0) }
    val animateLeft = remember { Animatable(steps[0].target.left) }
    val animateTop = remember { Animatable(steps[0].target.top) }
    val animateRight = remember { Animatable(steps[0].target.right) }
    val animateBottom = remember { Animatable(steps[0].target.bottom) }

    LaunchedEffect(index) {
        val t = steps[index].target
        launch { animateLeft.animateTo(t.left, tween(450)) }
        launch { animateTop.animateTo(t.top, tween(450)) }
        launch { animateRight.animateTo(t.right, tween(450)) }
        launch { animateBottom.animateTo(t.bottom, tween(450)) }
    }

    val density = LocalDensity.current
    val radiusPx = with(density) { 12.dp.toPx() }
    val paddingPx = with(density) { 8.dp.toPx() }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
        ) {
            drawRect(color = Color.Black.copy(alpha = 0.70f))
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(animateLeft.value - paddingPx, animateTop.value - paddingPx),
                size = Size(
                    (animateRight.value - animateLeft.value) + paddingPx * 2,
                    (animateBottom.value - animateTop.value) + paddingPx * 2,
                ),
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                blendMode = BlendMode.Clear,
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRoundRect(
                color = Color(0xFF009FB5),
                topLeft = Offset(animateLeft.value - paddingPx, animateTop.value - paddingPx),
                size = Size(
                    (animateRight.value - animateLeft.value) + paddingPx * 2,
                    (animateBottom.value - animateTop.value) + paddingPx * 2,
                ),
                cornerRadius = CornerRadius(radiusPx, radiusPx),
                style = Stroke(width = with(density) { 2.dp.toPx() }),
            )
        }

        val step = steps[index]
        val bubbleOffsetY = if (step.arrowUp) {
            animateBottom.value + paddingPx + with(density) { 18.dp.toPx() }
        } else {
            animateTop.value - paddingPx - with(density) { 200.dp.toPx() }
        }

        CoachBubble(
            title = step.title,
            description = step.description,
            arrowUp = step.arrowUp,
            arrowX = (animateLeft.value + animateRight.value) / 2f,
            isLast = index == steps.lastIndex,
            modifier = Modifier
                .offset { IntOffset(0, bubbleOffsetY.roundToInt()) }
                .padding(horizontal = 24.dp)
                .widthIn(max = 400.dp)
                .fillMaxWidth(),
            onNext = { if (index < steps.lastIndex) index++ else onFinish() },
            onSkip = onFinish,
        )
    }
}

@Composable
private fun CoachBubble(
    title: String,
    description: String,
    arrowUp: Boolean,
    arrowX: Float,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    onNext: () -> Unit,
    onSkip: () -> Unit,
) {
    Column(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxWidth().height(10.dp)) {
            val x = arrowX.coerceIn(28f, size.width - 28f)
            val path = Path().apply {
                if (arrowUp) {
                    moveTo(x - 12f, 0f); lineTo(x + 12f, 0f); lineTo(x, 10.dp.toPx())
                } else {
                    moveTo(x - 12f, size.height); lineTo(x + 12f, size.height)
                    lineTo(x, size.height - 10.dp.toPx())
                }
                close()
            }
            drawPath(path, Color(0xFF1D2530))
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1D2530))
                .padding(20.dp)
        ) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Spacer(Modifier.height(8.dp))
            Text(description, color = Color(0xFFB6C2D2), fontSize = 14.sp, lineHeight = 21.sp)
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = onSkip) { Text("跳过", color = Color(0xFF8A96A8)) }
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009FB5)),
                ) { Text(if (isLast) "开始使用" else "下一步") }
            }
        }
    }
}
