package com.example.happyluckydraw.ui.compnents

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.happyluckydraw.R
import com.example.happyluckydraw.entry.BottomTabs


@Composable
fun LuckBottomBar(tab: BottomTabs, onTabChange: (tab: BottomTabs) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        BottomBarItem(
            Modifier.clickable {
                onTabChange(BottomTabs.CIRCLE)
            },
            R.drawable.ic_tab_circle_30,
            "圆盘",
            if (tab == BottomTabs.CIRCLE) MaterialTheme.colorScheme.primary else Color.Black
        )
        BottomBarItem(
            Modifier.clickable {
                onTabChange(BottomTabs.SCRATCH)
            },
            R.drawable.ic_tab_scratch_30,
            "刮刮乐",
            if (tab == BottomTabs.SCRATCH) MaterialTheme.colorScheme.primary else Color.Black
        )
        BottomBarItem(
            Modifier.clickable {
                onTabChange(BottomTabs.SQUARE)
            },
            R.drawable.ic_tab_square_30,
            "方形",
            if (tab == BottomTabs.SQUARE) MaterialTheme.colorScheme.primary else Color.Black
        )
    }
}

@Composable
private fun BottomBarItem(
    modifier: Modifier = Modifier, @DrawableRes id: Int, contentDescription: String, tint: Color
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(id),
            contentDescription = contentDescription,
            modifier = Modifier.size(30.dp, 30.dp),
            tint = tint
        )
        Text(text = contentDescription, color = tint, fontSize = 12.sp)
    }
}

@Preview
@Composable
fun Preview() {
    LuckBottomBar(tab = BottomTabs.CIRCLE) {}
}