package com.example.cocktails.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        val shakerRotation = remember { Animatable(0f) }
        val shakerTilt = remember { Animatable(0f) }
        val liquidLevel = remember { Animatable(0f) }
        
        LaunchedEffect(Unit) {
            // Phase 1: Shake
            repeat(5) {
                shakerRotation.animateTo(15f, animationSpec = tween(100, easing = LinearEasing))
                shakerRotation.animateTo(-15f, animationSpec = tween(100, easing = LinearEasing))
            }
            shakerRotation.animateTo(0f, animationSpec = tween(100))

            // Phase 2: Tilt to pour
            shakerTilt.animateTo(120f, animationSpec = tween(500, easing = FastOutSlowInEasing))

            // Phase 3: Pour liquid
            liquidLevel.animateTo(1f, animationSpec = tween(1000, easing = LinearEasing))
            
            delay(500)
            onAnimationFinished()
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            // Draw Glass (Stationary)
            val glassColor = Color.LightGray.copy(alpha = 0.5f)
            val liquidColor = Color(0xFFFF5722) // Deep Orange Cocktail
            
            val glassTopY = centerY + 100f
            val glassHeight = 100f
            val glassWidth = 120f
            
            // Glass Bowl
            val glassPath = Path().apply {
                moveTo(centerX - glassWidth/2, glassTopY)
                lineTo(centerX + glassWidth/2, glassTopY)
                lineTo(centerX, glassTopY + glassHeight)
                close()
            }
            
            // Glass Stem & Base
            drawLine(
                color = glassColor,
                start = Offset(centerX, glassTopY + glassHeight),
                end = Offset(centerX, glassTopY + glassHeight + 80f),
                strokeWidth = 8f
            )
            drawLine(
                color = glassColor,
                start = Offset(centerX - 40f, glassTopY + glassHeight + 80f),
                end = Offset(centerX + 40f, glassTopY + glassHeight + 80f),
                strokeWidth = 8f
            )
            drawPath(path = glassPath, color = glassColor)

            // Draw Liquid in Glass
            if (liquidLevel.value > 0f) {
                val currentLiquidHeight = glassHeight * liquidLevel.value
                val currentLiquidWidth = glassWidth * liquidLevel.value
                val liquidTopY = (glassTopY + glassHeight) - currentLiquidHeight
                
                val liquidPath = Path().apply {
                    moveTo(centerX - currentLiquidWidth/2, liquidTopY)
                    lineTo(centerX + currentLiquidWidth/2, liquidTopY)
                    lineTo(centerX, glassTopY + glassHeight)
                    close()
                }
                drawPath(path = liquidPath, color = liquidColor)
            }

            // Draw Shaker
            val shakerColor = Color.Gray
            val shakerWidth = 80f
            val shakerHeight = 140f
            
            // Position shaker above and to the left initially
            val shakerX = centerX - 100f
            val shakerY = centerY - 100f

            translate(left = shakerX, top = shakerY) {
                rotate(degrees = shakerRotation.value + shakerTilt.value, pivot = Offset(0f, 0f)) {
                    // Shaker Body
                    drawRect(
                        color = shakerColor,
                        topLeft = Offset(-shakerWidth/2, -shakerHeight/2),
                        size = Size(shakerWidth, shakerHeight)
                    )
                    // Shaker Cap
                    drawArc(
                        color = shakerColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(-shakerWidth/2, -shakerHeight/2 - 20f),
                        size = Size(shakerWidth, 40f)
                    )
                    
                    // Pour Stream
                    if (shakerTilt.value > 100f && liquidLevel.value < 1f) {
                         // Calculate stream start point relative to rotated shaker
                         // Simple approximation: Stream falls straight down from the "top" (now side) of shaker
                         // Since we are inside the rotate block, drawing "down" is actually relative to the shaker's rotation.
                         // To draw a vertical stream in world coordinates, we need to be outside or counter-rotate.
                         // Easier approach: Draw stream outside the rotate block.
                    }
                }
            }
            
            // Draw Stream (Outside rotation to fall straight down)
            if (shakerTilt.value > 100f && liquidLevel.value < 1f) {
                // Calculate the exact position of the shaker's tip (spout) in world coordinates
                // The shaker is rotated around its center (0,0 in local space)
                // The tip is at (0, -shakerHeight/2 - 20f) in local space (top of cap)
                val totalRotation = shakerRotation.value + shakerTilt.value
                val angleRad = totalRotation * (PI / 180f)
                
                val tipLocalY = -shakerHeight / 2 - 20f
                
                // Rotate the tip point
                // x' = x*cos - y*sin
                // y' = x*sin + y*cos
                // Since x is 0:
                val tipRotatedX = -tipLocalY * sin(angleRad).toFloat()
                val tipRotatedY = tipLocalY * cos(angleRad).toFloat()
                
                val tipX = shakerX + tipRotatedX
                val tipY = shakerY + tipRotatedY
                
                // Liquid falls straight down due to gravity
                val liquidSurfaceY = glassTopY + glassHeight - (glassHeight * liquidLevel.value)
                
                // Draw the stream
                drawLine(
                    color = liquidColor,
                    start = Offset(tipX, tipY),
                    end = Offset(tipX, liquidSurfaceY),
                    strokeWidth = 8f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    }
}
