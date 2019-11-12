package com.denis.calculator.operators

import net.objecthunter.exp4j.operator.Operator
import kotlin.math.pow

class DegreeOperator: Operator("^", 2, true, PRECEDENCE_POWER + 1){
    override fun apply(vararg args: Double): Double {
        var degree: Double = args[1]
        var number: Double = args[0]

        return number.pow(degree)
    }
}