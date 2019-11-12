package com.denis.calculator.services

import com.denis.calculator.ExpressionResultViewModel
import com.denis.calculator.functions.FuncLg
import com.denis.calculator.functions.FuncLn
import com.denis.calculator.operators.DegreeOperator
import com.denis.calculator.operators.OperatorFactorial
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.*

class ExpressionService(private val viewModel: ExpressionResultViewModel){

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

        tryCalculateResult()
    }

    fun addOperator(operator: String){
        viewModel.clearExpressionData()

        if(viewModel.isOperatorActive){
            removeLastSymbol()
        }

        viewModel.addExpressionData(viewModel.getResultValue())
        updateExpressionAndResultData(operator)
        viewModel.isOperatorActive = true
    }

    fun addFunction(function: String){
        if(function == ")"){
            rightBracketListener(function)
            return
        }

        viewModel.clearExpressionData()

        if(viewModel.expression.value != "0" && !viewModel.isOperatorActive){
            return
        }

        if(viewModel.expression.value == "0"){
            viewModel.expression.value = ""
        }

        updateExpressionAndResultData(function)
        viewModel.bracketsToClose++
    }

    fun clearExpression(){
        viewModel.resultValue.value = "0"
        viewModel.expression.value = "0"
        viewModel.isOperatorActive = false
        viewModel.bracketsToClose = 0
    }
    fun removeLastSymbol(){
        if(viewModel.expression.value!!.isNotEmpty()){
            viewModel.expression.value = viewModel.expression.value!!.substring(0, viewModel.expression.value!!.length - 1)
        } else {
            viewModel.expression.value = "Cannot remove last symbol"
        }

        viewModel.expression.value = ""
    }

    fun calculateResult(){
        try {
            var actualExpression = viewModel.expression.value!!.toLowerCase(Locale.ENGLISH)

            while (viewModel.bracketsToClose > 0){
                actualExpression += ")"
                viewModel.bracketsToClose--
            }

            val expression = ExpressionBuilder(actualExpression)
                .functions(FuncLg(), FuncLn())
                .operator(OperatorFactorial(), DegreeOperator())
                .build()
            val result = expression.evaluate()
            val longResult = result.toLong()

            if(longResult.toDouble() == result){
                viewModel.updateResultValue(longResult.toString())
            } else {
                var resultText = result.toString()
                if(resultText.length > 8){
                    resultText = resultText.substring(0, 8)
                }
                viewModel.updateResultValue(resultText)
            }

            viewModel.expression.value = actualExpression

        } catch (ex: Exception) {
            viewModel.updateResultValue("Error")
        }

        viewModel.isResultFocused = true
    }

    private fun tryCalculateResult(){
        calculateResult()
        viewModel.isResultFocused = false
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
        val regex = Regex("([1-9]+)|(\\d+\\.)\$")

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

    private fun updateExpressionAndResultData(data: String){
        if(viewModel.isResultFocused){
            viewModel.updateResultValue("")
        }

        viewModel.addExpressionData(data)
    }
    private fun rightBracketListener(rightBracket: String){
        viewModel.clearExpressionData()

        if(viewModel.bracketsToClose > 0){
            updateExpressionAndResultData(rightBracket)
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