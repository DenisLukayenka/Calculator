package com.denis.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel(){
    val expression: MutableLiveData<String> = MutableLiveData("")
    val result: MutableLiveData<String> = MutableLiveData("")
    var isCommandActive: Boolean = false

    fun addExpressionData(data: String){
        expression.value += data
    }

    fun updateResultValue(data: String){
        result.value = data
    }
}