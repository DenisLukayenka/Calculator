package com.denis.calculator.functions

import net.objecthunter.exp4j.function.Function
import kotlin.math.log10

class FuncLg: Function("lg", 1){
    override fun apply(vararg args: Double): Double {
        return log10(args[0])
    }
}