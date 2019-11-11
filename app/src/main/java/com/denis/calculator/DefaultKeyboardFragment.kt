package com.denis.calculator

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.denis.calculator.databinding.DefaultKeyboardFragmentBinding
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.*
import kotlin.math.absoluteValue


class DefaultKeyboardFragment : Fragment() {
    private lateinit var binding: DefaultKeyboardFragmentBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.default_keyboard_fragment,
            container,
            false)

        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        setNumericListeners()
        setCommandListeners()
        setAdvancedListeners()

        return binding.root
    }

    private fun calculateResultValue(){
        try {
            val expression = ExpressionBuilder(viewModel.expression.value!!.toLowerCase(Locale.ENGLISH)).build()
            val result = expression.evaluate()
            val longResult = result.toLong()

            if(longResult.toDouble() == result){
                viewModel.updateResultValue("= $longResult")
            } else {
                viewModel.updateResultValue("= $result")
            }

        } catch (ex: Exception) {
            viewModel.updateResultValue("Error")
        }
    }
    private fun switchToAdvancedKeyboard(){
        val advancedKeyboard = AdvancedKeyboardFragment()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.replace(R.id.fragmentsLayout, advancedKeyboard)
        transaction?.commit()
    }
    private fun removeLastSymbol(){
        val expressionCommand = viewModel.expression.value!!

        if(expressionCommand.isNotEmpty()){
            viewModel.expression.value = expressionCommand.substring(0, expressionCommand.length - 1)
        }

        viewModel.result.value = ""
    }
    private fun clearExpressionCommandAndResult(){
        viewModel.result.value = ""
        viewModel.expression.value = ""
        viewModel.isCommandActive = false
    }

    private fun appendExpressionCommand(command: String){
        removeExpressionData()

        if(viewModel.isCommandActive){
            val expression = viewModel.expression.value!!
            if(expression.isNotEmpty()){
                viewModel.expression.value = expression.substring(0, expression.length - 1)
            }
        }

        var actualResultValue = viewModel.result.value!!
        if (actualResultValue.isNotEmpty()) {
            actualResultValue = actualResultValue.substring(2, actualResultValue.length)
        }

        viewModel.addExpressionData(actualResultValue)
        updateExpressionAndResultData(command)
        viewModel.isCommandActive = true
    }

    private fun appendNumberValue(number: String){
        removeExpressionData()
        val regex = Regex("(\\d*0)\$")
        val expression = viewModel.expression.value!!

        if(regex.containsMatchIn(expression)){
            viewModel.expression.value = expression.substring(0, expression.length - 1)
        }

        updateExpressionAndResultData(number)
        viewModel.isCommandActive = false
    }

    private fun appendDotValue(dot: String){
        if(!viewModel.isCommandActive && viewModel.result.value!!.isEmpty() && viewModel.expression.value!!.isNotEmpty()){
            val regex = Regex("\\d+(\\.)\\d*\$")

            if(!regex.containsMatchIn(viewModel.expression.value!!)){
                updateExpressionAndResultData(dot)
                viewModel.isCommandActive = true
            }
        }
    }

    private fun appendZeroValue(zero: String){
        removeExpressionData()

        val regex = Regex("([1-9]+)|(\\d+\\.)\$")

        if(regex.containsMatchIn(viewModel.expression.value!!)
            || viewModel.isCommandActive
            || viewModel.expression.value!!.isEmpty()){
            updateExpressionAndResultData(zero)
        }
    }

    private fun removeExpressionData(){
        if(viewModel.result.value!!.isNotEmpty()){
            viewModel.expression.value = ""
        }
    }
    private fun updateExpressionAndResultData(data: String){
        viewModel.updateResultValue("")
        viewModel.addExpressionData(data)
    }

    private fun setNumericListeners(){
        binding.apply {
            button0.setOnClickListener { appendZeroValue("0") }
            button1.setOnClickListener { appendNumberValue("1") }
            button2.setOnClickListener { appendNumberValue("2") }
            button3.setOnClickListener { appendNumberValue("3") }
            button4.setOnClickListener { appendNumberValue("4") }
            button5.setOnClickListener { appendNumberValue("5") }
            button6.setOnClickListener { appendNumberValue("6") }
            button7.setOnClickListener { appendNumberValue("7") }
            button8.setOnClickListener { appendNumberValue("8") }
            button9.setOnClickListener { appendNumberValue("9") }
            buttonDot.setOnClickListener { appendDotValue(".") }
        }
    }
    private fun setCommandListeners(){
        binding.apply {
            buttonMinus.setOnClickListener { appendExpressionCommand("-") }
            buttonMultiply.setOnClickListener { appendExpressionCommand("*") }
            buttonDivide.setOnClickListener { appendExpressionCommand("/") }
            buttonModulo.setOnClickListener { appendExpressionCommand("%") }
            buttonPlus.setOnClickListener { appendExpressionCommand("+") }
        }
    }

    private fun setAdvancedListeners(){
        binding.apply {
            buttonFragmentAdvanced.setOnClickListener{ switchToAdvancedKeyboard() }
            buttonResult.setOnClickListener { calculateResultValue() }
            buttonRemove.setOnClickListener { removeLastSymbol() }
            buttonClear.setOnClickListener { clearExpressionCommandAndResult() }
        }
    }
}
