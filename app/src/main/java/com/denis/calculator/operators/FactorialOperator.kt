package com.denis.calculator.operators

import net.objecthunter.exp4j.operator.Operator

class OperatorFactorial: Operator("!", 1, true, PRECEDENCE_POWER + 1){
    override fun apply(vararg args: Double): Double {
        val arg: Int = args[0].toInt()

        require(arg.toDouble() == args[0]) { "Operand for factorial has to be an integer" }
        require(arg >= 0) { "The operand of the factorial can not be less than zero" }

        var result: Double = 1.0

        for (i in 1..arg) {
            result *= i
        }

        return result
    }
}