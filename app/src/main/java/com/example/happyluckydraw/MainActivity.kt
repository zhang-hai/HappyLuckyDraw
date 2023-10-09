package com.example.happyluckydraw

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.happyluckydraw.ui.theme.HappyLuckyDrawTheme
import com.example.happyluckydraw.view.ScratchCardView
import com.example.happyluckydraw.view.TurntableView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HappyLuckyDrawTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainLayout()
                }
            }
        }
    }
}


@Composable
fun MainLayout(){
    //1:刮刮卡 2：转盘抽奖
    var type by remember { mutableStateOf(2) }

    val config = Resources.getSystem().configuration
    val width by remember {
        mutableStateOf(if (config.orientation==Configuration.ORIENTATION_LANDSCAPE) config.screenHeightDp else config.screenWidthDp)
    }
    Box{
        Row(Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            when(type){
                1 ->{//刮刮卡模式
                    ScratchCardView(Modifier.fillMaxSize())
                }
                2 ->{//大转盘模式
                    TurntableView(modifier = Modifier
                        .size(width.dp, width.dp)
                        .padding(5.dp),
                        false)
                }
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .offset((config.screenWidthDp - 60).dp, (config.screenHeightDp - 150).dp),
            shape = CircleShape,
            contentColor = MaterialTheme.colorScheme.primary,
            onClick = {
            type = if (type == 1) 2 else 1
        }) {
            Text(text = if(type == 1) "转盘" else "刮刮卡")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HappyLuckyDrawTheme {
//        Greeting("Android")
        MainLayout()
    }
}