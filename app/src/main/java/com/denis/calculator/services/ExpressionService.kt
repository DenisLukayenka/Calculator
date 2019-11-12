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
        when(number){
            "0" -> addZeroNumber(number)
            "." -> addDotValue(number)

            else -> {
                viewModel.clearExpressionData()
                val regex = Regex("\\W0\$")
                val floatNumberRegex = Regex("\\d+(\\.)\\d*\$")

                val expression = viewModel.expression.value!!

                if((regex.containsMatchIn(expression) && !floatNumberRegex.containsMatchIn(expression))
                    || expression == "0"){
                    removeLastSymbol()
                }

                updateExpressionAndResultData(number)
                viewModel.isCommandActive = false
            }
        }
    }

    fun addOperator(operator: String){
        viewModel.clearExpressionData()

        if(viewModel.isCommandActive){
            removeLastSymbol()
        }

        viewModel.addExpressionData(viewModel.getResultValue())
        updateExpressionAndResultData(operator)
        viewModel.isCommandActive = true
    }

    fun addFunction(function: String){
        if(function == ")"){
            rightBracketListener(function)
            return
        }

        viewModel.clearExpressionData()

        if(viewModel.expression.value != "0" && !viewModel.isCommandActive){
            return
        }

        if(viewModel.expression.value == "0"){
            viewModel.expression.value = ""
        }

        updateExpressionAndResultData(function)
        viewModel.bracketsToClose++
    }

    fun clearExpression(){
        viewModel.resultValue.value = ""
        viewModel.expression.value = "0"
        viewModel.isCommandActive = false
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
    }

    private fun addZeroNumber(zero: String){
        val regex = Regex("([1-9]+)|(\\d+\\.)\$")

        if(regex.containsMatchIn(viewModel.expression.value!!)
            || viewModel.isCommandActive
            || viewModel.expression.value!!.isEmpty()){

            viewModel.clearExpressionData()
            updateExpressionAndResultData(zero)
            viewModel.isCommandActive = false
        }
    }
    private fun addDotValue(dot: String){
        if(!viewModel.isCommandActive && viewModel.resultValue.value!!.isEmpty() && viewModel.expression.value!!.isNotEmpty()){
            val regex = Regex("\\d+(\\.)\\d*\$")

            if(!regex.containsMatchIn(viewModel.expression.value!!)){
                updateExpressionAndResultData(".")
                viewModel.isCommandActive = true
            }
        }
    }
    private fun updateExpressionAndResultData(data: String){
        viewModel.updateResultValue("")
        viewModel.addExpressionData(data)
    }
    private fun rightBracketListener(rightBracket: String){
        viewModel.clearExpressionData()

        if(viewModel.bracketsToClose > 0){
            updateExpressionAndResultData(rightBracket)
            viewModel.bracketsToClose--
        }
    }
}