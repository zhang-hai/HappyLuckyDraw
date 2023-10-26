package com.example.happyluckydraw

import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.happyluckydraw.entry.BottomTabs
import com.example.happyluckydraw.ui.compnents.LuckBottomBar
import com.example.happyluckydraw.ui.theme.HappyLuckyDrawTheme
import com.example.happyluckydraw.view.ScratchCardView
import com.example.happyluckydraw.view.TurntableView
import com.example.happyluckydraw.vm.LuckViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HappyLuckyDrawTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainLayout()
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MainLayout() {
        val luckViewModel: LuckViewModel = viewModel()
        val config = Resources.getSystem().configuration
        val width by remember {
            mutableStateOf(if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) config.screenHeightDp else config.screenWidthDp)
        }
        val pagerState = rememberPagerState()
        LaunchedEffect(pagerState) {
            // Collect from the a snapshotFlow reading the currentPage
            snapshotFlow { pagerState.currentPage }.collect { page ->
                luckViewModel.tabIndex = when (page) {
                    BottomTabs.CIRCLE.ordinal -> BottomTabs.CIRCLE
                    BottomTabs.SCRATCH.ordinal -> BottomTabs.SCRATCH
                    BottomTabs.SQUARE.ordinal -> BottomTabs.SQUARE
                    else -> BottomTabs.CIRCLE
                }
            }
        }
        val coroutineScope = rememberCoroutineScope()
        val content = LocalContext.current
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HorizontalPager(
                pageCount = 3,
                state = pagerState,
                modifier = Modifier.weight(1f),
            ) { page ->
                when (page) {
                    //刮刮卡模式
                    BottomTabs.CIRCLE.ordinal -> {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            TurntableView(
                                modifier = Modifier
                                    .size(width.dp, width.dp)
                                    .padding(5.dp),
                                false
                            )
                        }
                    }
                    //大转盘模式
                    BottomTabs.SCRATCH.ordinal -> {
                        ScratchCardView(Modifier.fillMaxSize())
                    }

                    BottomTabs.SQUARE.ordinal -> {
                        Box(Modifier.fillMaxSize()) {

                        }
                    }
                }
            }
            LuckBottomBar(tab = luckViewModel.tabIndex) {
                println("点击了：$it")
                luckViewModel.tabIndex = it
                coroutineScope.launch {
                    pagerState.scrollToPage(it.ordinal)
                }
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
}
