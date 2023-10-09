package com.example.happyluckydraw.view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.happyluckydraw.entry.MotionEvent
import com.example.happyluckydraw.entry.PathProperties
import com.example.happyluckydraw.ui.theme.CoverColor
import com.example.happyluckydraw.ui.theme.Transparent
import com.example.happyluckydraw.util.LuckDrawImpl

private fun isInRect(w:Float,h:Float,pos: Offset) = (pos.x in 0.0f..w) && (pos.y in 0.0f .. h)


//刮刮卡抽奖
@Composable
fun ScratchCardView(modifier: Modifier){
    var currentPosition by remember { mutableStateOf(Offset.Unspecified) }
    var previousPosition by remember { mutableStateOf(Offset.Unspecified) }
    var motionEvent by remember { mutableStateOf(MotionEvent.Idle) }
    var currentPath by remember { mutableStateOf(Path()) }
    var currentPathProperty by remember { mutableStateOf(PathProperties(eraseMode = true)) }

    val paths = remember { mutableStateListOf<Pair<Path, PathProperties>>() }
    var reward by remember {
        mutableStateOf(LuckDrawImpl.lotteryReward())
    }

    Column(modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "刮奖区", color = MaterialTheme.colorScheme.primary, fontSize = 30.sp, fontWeight = FontWeight(500))
        //显示刮刮卡
        Box(
            modifier = Modifier
                .size(300.dp, 150.dp),
            contentAlignment = Alignment.Center
        ){
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(painter = painterResource(id = reward.id),modifier= Modifier.size(70.dp), contentDescription = reward.name)
                Text(text = reward.name, color = MaterialTheme.colorScheme.primary,fontSize = 20.sp, fontWeight = FontWeight(500))
            }
            val modifier0 = Modifier
                .fillMaxSize()
                .background(Transparent)
                .pointerInput("dragging") {
                    detectDragGestures(onDragStart = {
//                                    currentPosition = it
//                                    motionEvent = MotionEvent.Down
                    }, onDrag = { change, _ ->
                        currentPosition = change.position
                        if (motionEvent == MotionEvent.Idle) {
                            motionEvent = MotionEvent.Down
                        } else {
                            motionEvent = MotionEvent.Move
                        }

                        change.consume()
                    }, onDragEnd = {
                        motionEvent = MotionEvent.Up
                    })
                }
            Canvas(modifier = modifier0){
                when(motionEvent){
                    MotionEvent.Down->{
                        if(currentPosition != Offset.Unspecified){
                            currentPath.moveTo(currentPosition.x, currentPosition.y)
                        }
                        previousPosition = currentPosition
                        println("MotionEvent.Down->$currentPosition")
                    }
                    MotionEvent.Move->{
                        println("MotionEvent.Move->$previousPosition")
                        if(previousPosition != Offset.Unspecified && isInRect(size.width,size.height,previousPosition)){
                            currentPath.quadraticBezierTo(
                                previousPosition.x,
                                previousPosition.y,
                                (previousPosition.x + currentPosition.x) / 2,
                                (previousPosition.y + currentPosition.y) / 2

                            )
                        }
                        previousPosition = currentPosition
                    }
                    MotionEvent.Up->{
                        println("MotionEvent.Up->$currentPosition")
                        if(currentPosition != Offset.Unspecified && isInRect(size.width,size.height,currentPosition)){
                            currentPath.lineTo(currentPosition.x, currentPosition.y)
                        }
                        // Pointer is up save current path
                        paths.add(Pair(currentPath, currentPathProperty))
                        // Since paths are keys for map, use new one for each key
                        // and have separate path for each down-move-up gesture cycle
                        currentPath = Path()
                        // Create new instance of path properties to have new path and properties
                        // only for the one currently being drawn
                        currentPathProperty = PathProperties(
                            strokeWidth = currentPathProperty.strokeWidth,
                            color = currentPathProperty.color,
                            strokeCap = currentPathProperty.strokeCap,
                            strokeJoin = currentPathProperty.strokeJoin,
                            eraseMode = currentPathProperty.eraseMode
                        )

                        // If we leave this state at MotionEvent.Up it causes current path to draw
                        // line from (0,0) if this composable recomposes when draw mode is changed
                        currentPosition = Offset.Unspecified
                        previousPosition = currentPosition
                        motionEvent = MotionEvent.Idle
                    }
                    else -> Unit
                }
                drawIntoCanvas {
                    with(it.nativeCanvas){
                        val checkPoint = saveLayer(null, null)
                        drawRect(CoverColor)
                        paths.forEach {
                            val path = it.first
                            val property = it.second
                            if (!property.eraseMode) {
                                drawPath(
                                    color = property.color,
                                    path = path,
                                    style = Stroke(
                                        width = property.strokeWidth,
                                        cap = property.strokeCap,
                                        join = property.strokeJoin
                                    )
                                )
                            } else {
                                // Source
                                drawPath(
                                    color = Color.Transparent,
                                    path = path,
                                    style = Stroke(
                                        width = property.strokeWidth,
                                        cap = property.strokeCap,
                                        join = property.strokeJoin
                                    ),
                                    blendMode = BlendMode.Clear
                                )
                            }
                        }
                        if (motionEvent != MotionEvent.Idle) {
                            if (!currentPathProperty.eraseMode) {
                                drawPath(
                                    color = currentPathProperty.color,
                                    path = currentPath,
                                    style = Stroke(
                                        width = currentPathProperty.strokeWidth,
                                        cap = currentPathProperty.strokeCap,
                                        join = currentPathProperty.strokeJoin
                                    )
                                )
                            } else {
                                drawPath(
                                    color = Color.Transparent,
                                    path = currentPath,
                                    style = Stroke(
                                        width = currentPathProperty.strokeWidth,
                                        cap = currentPathProperty.strokeCap,
                                        join = currentPathProperty.strokeJoin
                                    ),
                                    blendMode = BlendMode.Clear
                                )
                            }
                        }
                        restoreToCount(checkPoint)
                    }
                }
            }
        }
        Box(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                //点击则将状态重新设置为未开始
                currentPath = Path()
                paths.clear()
                reward = LuckDrawImpl.lotteryReward()
            },
            Modifier.size(120.dp,40.dp),
            contentPadding= PaddingValues(horizontal = 20.dp, vertical = 5.dp)
        ) {
            Text(
                text = "再刮一次",
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
    }
}