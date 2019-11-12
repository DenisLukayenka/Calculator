package com.denis.calculator.functions

import net.objecthunter.exp4j.function.Function
import kotlin.math.ln

class FuncLn: Function("ln", 1){
    override fun apply(vararg args: Double): Double {
        return ln(args[0])
    }
}