package com.denis.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExpressionResultViewModel : ViewModel(){
    val expression: MutableLiveData<String> = MutableLiveData("0")
    val resultValue: MutableLiveData<String> = MutableLiveData("")

    var isCommandActive: Boolean = false
    var bracketsToClose: Int = 0

    fun addExpressionData(data: String){
        if(expression.value == "0"){
            expression.value = data
        } else {
            expression.value += data
        }
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
}