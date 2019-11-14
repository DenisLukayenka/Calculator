package com.denis.calculator.functions

import net.objecthunter.exp4j.function.Function
import kotlin.math.PI
import kotlin.math.tan

class FuncTan: Function("tan", 1){
    override fun apply(vararg args: Double): Double {
        val rad = args[0]
        val degrees = rad * PI / 180

        return tan(degrees)
    }
}