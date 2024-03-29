package com.denis.calculator.services

import com.denis.calculator.ExpressionResultViewModel
import com.denis.calculator.functions.*
import com.denis.calculator.operators.*
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import java.util.*
import kotlin.math.round

class ExpressionService(private val viewModel: ExpressionResultViewModel){
    private val regexFunctions =  Regex("((sin\\()|(cos\\()|(tan\\()|(ln\\()|(lg\\()|(√\\())\$")

    private val maxDoubleValueLength: Int = 10
    private val MAX_EXPRESSION_LENGTH = 13

    private val operators = "%/*-+^!"
    private val replaceFunctions = mapOf(
        "√" to "sqrt"
    )

    fun addNumber(number: String){
        if(viewModel.getExpressionValue().length >= MAX_EXPRESSION_LENGTH){
            return
        }

        if(viewModel.isResultFocused){
            resetFocusedResult("0")
        }

        // Move command here cause dot value don't reset operator to false
        viewModel.isOperatorActive = false

        when(number){
            "0" -> addZeroNumber(number)
            "." -> addDotValue(number)
            else -> addDefaultNumber(number)
        }

        tryShowCalculatedResult()
    }

    fun addOperator(operator: String){
        if(viewModel.getExpressionValue().length >= MAX_EXPRESSION_LENGTH){
            return
        }

        // Switch to new operator
        // TODO specific for '-' operator
        if(viewModel.isOperatorActive){
            removeLastSymbol(viewModel.getExpressionValue())
        }
        if(viewModel.isResultFocused){
            resetFocusedResult(viewModel.getResultValue())
        }

        viewModel.addExpressionData(operator)
        viewModel.isOperatorActive = true
    }

    fun addFunction(function: String){
        if(viewModel.getExpressionValue().length >= MAX_EXPRESSION_LENGTH){
            return
        }

        if(viewModel.isResultFocused){
            resetFocusedResult("0")
        }

        if(function == ")"){
            rightBracketListener(function)
            return
        }

        if(viewModel.expression.value == "0"){
            viewModel.expression.value = ""
        }

        viewModel.addExpressionData(function)
        viewModel.bracketsToClose++
        viewModel.isOperatorActive = false
    }

    fun clearExpression(){
        viewModel.resultValue.value = "0"
        viewModel.expression.value = "0"
        viewModel.isOperatorActive = false
        viewModel.bracketsToClose = 0
    }

    fun backspaceSymbol(){
        viewModel.isResultFocused = false

        val expression = viewModel.getExpressionValue()
        val lastSymbol = expression.last()

        if (regexFunctions.containsMatchIn(expression)){
            viewModel.clearExpressionData()

            val newExpression = regexFunctions.replaceFirst(expression, "")
            viewModel.addExpressionData(newExpression)
            viewModel.bracketsToClose--
        } else {
            when (lastSymbol) {
                in operators -> viewModel.isOperatorActive = false
                ')' -> viewModel.bracketsToClose++
                '(' -> viewModel.bracketsToClose--
            }

            removeLastSymbol(expression)
        }

        val newExpression = viewModel.getExpressionValue()
        if(newExpression.isEmpty()){
            viewModel.clearExpressionData()
            viewModel.addExpressionData("0")
        } else if(newExpression.last() in operators){
            viewModel.isOperatorActive = true
        }

        tryShowCalculatedResult()
    }

    fun calculateFinalResult(){
        val preparedExpression = prepareExpressionToCalculate(viewModel.getExpressionValue(), viewModel.bracketsToClose)
        val calculateResultValue = calculateResult(preparedExpression, 0)

        viewModel.updateResultValue(calculateResultValue)
        viewModel.clearExpressionData()
        viewModel.addExpressionData(preparedExpression)

        viewModel.bracketsToClose = 0
        viewModel.isResultFocused = true
    }

    private fun calculateResult(expr: String, brackets: Int): String{
        return try {
            var actualExpression = prepareExpressionToCalculate(expr, brackets)
            actualExpression = replaceToCorrectFunctions(actualExpression)

            val expression = createExpressionBuilder(actualExpression).build()

            val result = expression.evaluate()

            convertCalculateResultToString(result)

        } catch (ex: Exception) {
            "Error"
        }
    }

    private fun tryShowCalculatedResult(){
        val calculateResultValue = calculateResult(viewModel.getExpressionValue(), viewModel.bracketsToClose)
        viewModel.updateResultValue(calculateResultValue)
    }

    private fun createExpressionBuilder(expression: String): ExpressionBuilder {
        val operators = listOf(
            OperatorFactorial(),
            DegreeOperator())
        val functions = mutableListOf(
            FuncLn(),
            FuncLg())

        if(viewModel.isDeg){
            functions.apply {
                add(FuncSin())
                add(FuncCos())
                add(FuncTan())
            }
        }

        val expressionBuilder = ExpressionBuilder(expression)
        expressionBuilder.functions(functions)
        expressionBuilder.operator(operators)

        return expressionBuilder
    }

    private fun convertCalculateResultToString(result: Double): String{
        val actualResult = result.round(maxDoubleValueLength)
        val longResult = actualResult.toLong()

        return if(longResult.toDouble() == result){
            longResult.toString()
        } else {
            actualResult.toString()
        }
    }
    private fun prepareExpressionToCalculate(expression: String, bracketsCount: Int): String{
        var actualExpression = expression.toLowerCase(Locale.ENGLISH)
        repeat(bracketsCount){
            actualExpression += ")"
        }

        val lastSymbol = expression.last()
        if(lastSymbol in operators){
            actualExpression = actualExpression.substring(0, actualExpression.length - 1)
        }

        return actualExpression
    }
    private fun replaceToCorrectFunctions(expression: String): String{
        var actualExpression = expression

        for (entry in replaceFunctions){
            actualExpression = expression.replace(entry.key, entry.value, true)
        }

        return actualExpression
    }

    private fun resetFocusedResult(expressionData: String){
        viewModel.updateResultValue("")
        viewModel.clearExpressionData()
        viewModel.addExpressionData(expressionData)
        viewModel.bracketsToClose = 0
        viewModel.isOperatorActive = false
        viewModel.isResultFocused = false
    }

    private fun addDefaultNumber(number: String){
        // Switch 'zero' to new digit if it's not the float number
        // e.g. actual expression: 15+0, input number: 5 --> new expression: 15+5
        if(checkToRemoveLastZeroSymbol()){
            removeLastSymbol(viewModel.getExpressionValue())
        }

        viewModel.addExpressionData(number)
    }
    private fun addZeroNumber(zero: String){
        // Add zero only after integer number or in float part
        val regex = Regex("([1-9]+)|(\\d+\\.)|(\\w+\\()\$")

        // e.g.
        // 1. 15[0]  -- regex
        // 2. 15.[0] -- regex
        // 3. 15+[0] -- isOperatorActive
        if(regex.containsMatchIn(viewModel.expression.value!!)
            || viewModel.isOperatorActive){

            viewModel.addExpressionData(zero)
        }
    }
    private fun addDotValue(dot: String){
        if(!viewModel.isOperatorActive
            && !viewModel.isExpressionEmpty()){

            val regex = Regex("\\d+(\\.)\\d*\$")

            if(!regex.containsMatchIn(viewModel.getExpressionValue())){
                viewModel.addExpressionData(dot)
                viewModel.isOperatorActive = true
            }
        }
    }

    private fun rightBracketListener(rightBracket: String){
        if(viewModel.bracketsToClose > 0){
            viewModel.addExpressionData(rightBracket)
            viewModel.bracketsToClose--
            viewModel.isOperatorActive = false
        }
    }

    private fun checkToRemoveLastZeroSymbol(): Boolean {
        val regex = Regex("\\W0\$")
        val floatNumberRegex = Regex("\\d+(\\.)\\d*\$")
        val expression = viewModel.expression.value!!

        if((regex.containsMatchIn(expression) && !floatNumberRegex.containsMatchIn(expression))
            || expression == "0"){
            return true
        }

        return false
    }
    private fun removeLastSymbol(expression: String){
        viewModel.expression.value = expression.substring(0, expression.length - 1)
    }

    private fun Double.round(decimals: Int): Double {
        var multiplier = 1.0

        repeat(decimals){
            multiplier *= 10
        }

        return round(this * multiplier) / multiplier
    }

    fun onSelectedDeg(){
        viewModel.isDeg = true
        tryShowCalculatedResult()
    }

    fun onSelectedRad(){
        viewModel.isDeg = false
        tryShowCalculatedResult()
    }
}