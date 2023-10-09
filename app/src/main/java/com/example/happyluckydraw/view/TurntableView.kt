package com.example.happyluckydraw.view

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.painterResource
import com.example.happyluckydraw.R
import com.example.happyluckydraw.ui.theme.RED
import com.example.happyluckydraw.util.LuckDrawImpl
import kotlinx.coroutines.launch
import kotlin.math.PI

/**
 * 转盘抽奖
 * @param modifier 修饰
 * @param isTextArc 文字是否沿着圆环排列，true是，false不是
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TurntableView(modifier: Modifier, isTextArc:Boolean = true){
    val sweepAngle = 360f / LuckDrawImpl.list.size
    var clickable by remember { mutableStateOf(true) }
    var angle by remember{ mutableStateOf(0f) }
    var lastAngle by remember{ mutableStateOf(if(isTextArc) 0f else (180f-sweepAngle/2)) }
    //是否展示抽奖结果
    var rewardName by remember { mutableStateOf("") }
    val MIN_ANGLE:Float = 360*10f

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val rotateValue by animateFloatAsState(
        targetValue = lastAngle + angle,
        animationSpec = tween(durationMillis = 10000),
        label = ""
    ){
        clickable = true
        scope.launch {
            snackbarHostState.showSnackbar(rewardName)
        }
    }
    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost (snackbarHostState) }
    ) { _ ->
        Box(modifier = modifier, contentAlignment = Alignment.Center){
            Canvas(modifier = Modifier
                .fillMaxSize()){
                drawIntoCanvas {
                    var startAngle = -90f
                    val paint = Paint().apply{
                        color = android.graphics.Color.RED
                        strokeWidth = 2f * density
                        textSize = 10f * density
                        typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
                    }
                    val radius = size.width/2f
                    val rectF = RectF().apply {
                        top = -radius
                        left = -radius
                        right = radius
                        bottom = radius
                    }
                    //绘制扇形奖项
                    if (isTextArc){
                        for ((i, item) in LuckDrawImpl.list.withIndex()){
                            drawArc(
                                Color(LuckDrawImpl.colorList[i% LuckDrawImpl.colorList.size]),
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                size= Size(size.width,size.width),
                            )
                            startAngle += sweepAngle
                        }
                        startAngle = -90f-sweepAngle/2
                        with(it.nativeCanvas) {
                            val nVal = radius * PI / LuckDrawImpl.list.size / 2
                            val voffset = radius/ 2f / 6f
                            save()
                            translate(radius,radius)
                            for (item in LuckDrawImpl.list){
                                startAngle += sweepAngle
                                val path = Path()
                                val start = startAngle-sweepAngle/4
                                path.addArc(rectF,start,sweepAngle)
                                val txtW = paint.measureText(item.name)
                                val hoffset = (nVal - txtW / 2f).toFloat()
                                drawTextOnPath(item.name,path,hoffset,voffset,paint)
                            }
                            restore()
                        }
                    }else{
                        with(it.nativeCanvas){
                            save()
                            translate(radius,radius)
                            startAngle = -90f
                            for ((i, item) in LuckDrawImpl.list.withIndex()) {
                                save()
                                rotate(180f+startAngle)
                                paint.color = LuckDrawImpl.colorList[i% LuckDrawImpl.colorList.size]
                                drawArc(rectF,-sweepAngle/2,sweepAngle,true,paint)

                                paint.color = android.graphics.Color.RED
                                drawText(item.name,radius*2/3,0f,paint)
                                restore()
                                startAngle += sweepAngle
                            }
                            restore()
                        }
                    }
                }
                drawCircle(color = RED, style = Stroke(width=15f))

            }
            Image(
                painter = painterResource(id = R.mipmap.ic_rotate),
                contentDescription = "旋转",
                modifier= Modifier.rotate(rotateValue),
            )
            Image(
                painter = painterResource(id = R.mipmap.ic_btn_turn),
                contentDescription = "抽奖",
                modifier = Modifier.clickable(clickable){
                    //上次旋转的角度 + 新的要选择角度
                    lastAngle = rotateValue + MIN_ANGLE + (360 - angle)
                    val index = LuckDrawImpl.luckIndex()
                    angle = LuckDrawImpl.lotteryDegree(index)
                    rewardName = "恭喜你，抽中 ${LuckDrawImpl.lotteryReward(index).name}"
                    clickable = false
                    println("---->$lastAngle ==== $angle")
                }
            )
        }
    }
}