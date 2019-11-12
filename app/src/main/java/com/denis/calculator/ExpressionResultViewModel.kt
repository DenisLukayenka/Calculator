package com.denis.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExpressionResultViewModel : ViewModel(){
    val expression: MutableLiveData<String> = MutableLiveData("0")
    val resultValue: MutableLiveData<String> = MutableLiveData("0")
    val isResultOnFocus: MutableLiveData<Boolean> = MutableLiveData(false)

    var isResultFocused: Boolean
        get() = isResultOnFocus.value!!
        set(value) {
            isResultOnFocus.value = value
        }

    var isOperatorActive: Boolean = false
    var bracketsToClose: Int = 0

    fun addExpressionData(data: String){
        expression.value += data
    }

    fun updateResultValue(data: String){
        resultValue.value = data
    }

    fun clearExpressionData(){
        expression.value = ""
    }

    fun getResultValue(): String {
        return resultValue.value!!
    }

    fun getExpressionValue(): String{
        return expression.value!!
    }

    fun isExpressionEmpty(): Boolean{
        return expression.value!!.isEmpty()
    }
}