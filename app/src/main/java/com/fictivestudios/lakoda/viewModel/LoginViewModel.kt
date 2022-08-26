package com.fictivestudios.lakoda.viewModel

import android.widget.TextView
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    var text:String="abcd"
    fun setText(Text:TextView){
        Text.setText("")
    }
}