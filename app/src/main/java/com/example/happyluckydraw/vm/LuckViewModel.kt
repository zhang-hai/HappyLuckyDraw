package com.example.happyluckydraw.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.happyluckydraw.entry.BottomTabs

class LuckViewModel : ViewModel() {

    var tabIndex by mutableStateOf(BottomTabs.CIRCLE)

}