package com.denis.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel(){
    val command: MutableLiveData<String> = MutableLiveData("")
    val result: MutableLiveData<String> = MutableLiveData("")

    fun addNewData(data: String){
        command.value += data
    }

    fun updateResultValue(data: String){
        result.value = data
    }
}