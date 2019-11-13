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

    fun addNumber(number: String){
        if(viewModel.isResultFocused){

            // Unfocus result field.
            viewModel.updateResultValue("")
            viewModel.clearExpressionData()
            viewModel.isResultFocused = false
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
            removeLastSymbol()
        }

        if(viewModel.isResultFocused){
            viewModel.clearExpressionData()
            viewModel.addExpressionData(viewModel.getResultValue())
            viewModel.isResultFocused = false
        }

        viewModel.addExpressionData(operator)
        viewModel.isOperatorActive = true
    }

    fun addFunction(function: String){
        if(viewModel.isResultFocused){

            // Unfocus result field.
            viewModel.updateResultValue("")
            viewModel.clearExpressionData()
            viewModel.isResultFocused = false
        }

        if(function == ")"){
            rightBracketListener(function)
            return
        }

        if(viewModel.expression.value == "0"){
            viewModel.expression.value = ""
        }

        if(function == "("){
            viewModel.bracketsToClose++
        }

        viewModel.addExpressionData(function)
        viewModel.bracketsToClose++
    }

    fun clearExpression(){
        viewModel.resultValue.value = "0"
        viewModel.expression.value = "0"
        viewModel.isOperatorActive = false
        viewModel.bracketsToClose = 0
    }

    private fun removeLastSymbol(){
        val expression = viewModel.getExpressionValue()
        if(!viewModel.isExpressionEmpty()){
            viewModel.expression.value = expression.substring(0, expression.length - 1)
        } else {
            viewModel.expression.value = "Cannot remove last symbol"
        }
    }

    fun backspaceSymbol(){
        val expression = viewModel.getExpressionValue()
        if(expression.length <= 1){
            viewModel.expression.value = "0"
        } else {
            viewModel.expression.value = expression.substring(0, expression.length - 1)
        }
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
            removeLastSymbol()
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