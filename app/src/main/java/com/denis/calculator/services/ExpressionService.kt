package com.denis.calculator.services

import com.denis.calculator.ExpressionResultViewModel
import com.denis.calculator.functions.FuncLg
import com.denis.calculator.functions.FuncLn
import com.denis.calculator.operators.DegreeOperator
import com.denis.calculator.operators.OperatorFactorial
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.*

class ExpressionService(private val viewModel: ExpressionResultViewModel){
    private val maxDoubleValueLength: Int = 10
    private val operators = "%/*-+^!"
    private val regexFunctions =  Regex("(sin\\()|(cos\\()|(tan\\()|(ln\\()|(lg\\()\$")

    fun addNumber(number: String){
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

    private fun removeLastSymbol(expression: String){
        viewModel.expression.value = expression.substring(0, expression.length - 1)
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
        }

        removeLastSymbol(expression)
        val newExpression = viewModel.getExpressionValue()

        if(newExpression.isEmpty()){
            viewModel.clearExpressionData()
            viewModel.addExpressionData("0")
        } else if(newExpression.last() in operators){
            viewModel.isOperatorActive = true
        }

        tryShowCalculatedResult()
    }

    private fun resetFocusedResult(expressionData: String){
        viewModel.updateResultValue("")
        viewModel.clearExpressionData()
        viewModel.addExpressionData(expressionData)
        viewModel.bracketsToClose = 0
        viewModel.isOperatorActive = false
        viewModel.isResultFocused = false
    }

    private fun convertCalculateResultToString(result: Double): String{
        val longResult = result.toLong()

        return if(longResult.toDouble() == result){
            longResult.toString()
        }else{
            var resultText = result.toString()
            if(resultText.length > maxDoubleValueLength){
                resultText = resultText.substring(0, maxDoubleValueLength)
            }
            resultText
        }
    }
    private fun prepareExpressionToCalculate(expression: String, bracketsCount: Int): String{
        var actualExpression = expression.toLowerCase(Locale.ENGLISH)
        var bracketsToAdd = bracketsCount

        while (bracketsToAdd > 0){
            actualExpression += ")"
            bracketsToAdd--
        }

        val lastSymbol = expression.last()
        if(lastSymbol in operators){
            actualExpression = actualExpression.substring(0, actualExpression.length - 1)
        }

        return actualExpression
    }

    private fun calculateResult(expr: String, brackets: Int): String{
        return try {
            val actualExpression = prepareExpressionToCalculate(expr, brackets)
            val expression = ExpressionBuilder(actualExpression)
                .functions(FuncLg(), FuncLn())
                .operator(OperatorFactorial(), DegreeOperator())
                .build()

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

    fun calculateFinalResult(){
        val preparedExpression = prepareExpressionToCalculate(viewModel.getExpressionValue(), viewModel.bracketsToClose)
        val calculateResultValue = calculateResult(preparedExpression, 0)

        viewModel.updateResultValue(calculateResultValue)
        viewModel.clearExpressionData()
        viewModel.addExpressionData(preparedExpression)

        viewModel.bracketsToClose = 0
        viewModel.isResultFocused = true
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
}