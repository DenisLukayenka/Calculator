package com.denis.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel(){
    val expression: MutableLiveData<String> = MutableLiveData("0")
    val resultValue: MutableLiveData<String> = MutableLiveData("")

    var isCommandActive: Boolean = false
    var bracketsToClose: Int = 0

    fun addExpressionData(data: String){
        expression.value += data
    }

    fun updateResultValue(data: String){
        resultValue.value = data
    }

    fun clearExpressionData(){
        if(resultValue.value!!.isNotEmpty()){
            expression.value = "0"
        }
    }

    fun getResultValue(): String {
        return resultValue.value!!
    }

    fun removeLastExpressionSymbol(){
        if(expression.value!!.isNotEmpty()){
            expression.value = expression.value!!.substring(0, expression.value!!.length - 1)
        } else {
            expression.value = "Cannot remove last symbol"
        }

        resultValue.value = ""
    }
}