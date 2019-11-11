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
    private var isCommandActive: Boolean = false

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
                viewModel.updateResultValue("= ${result.toString().substring(0, 8)}")
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
        isCommandActive = false
    }

    private fun appendExpressionCommand(command: String){
        removeExpressionData()

        if(!isCommandActive) {
            var actualResultValue = viewModel.result.value!!
            if (actualResultValue.isNotEmpty()) {
                actualResultValue = actualResultValue.substring(2, actualResultValue.length)
            }

            viewModel.addExpressionData(actualResultValue)
            updateExpressionAndResultData(command)
            isCommandActive = true
        }
    }

    private fun appendNumberValue(number: String){
        removeExpressionData()

        if(viewModel.expression.value!!.isEmpty() ||
            (viewModel.expression.value!!.isNotEmpty() && viewModel.expression.value!!.last() != '0')){
            updateExpressionAndResultData(number)
            isCommandActive = false
        }
    }

    private fun appendDotValue(dot:String){
        if(!isCommandActive){
            val regex = Regex("\\d+(\\.)\\d*\$")

            if(!regex.containsMatchIn(viewModel.expression.value!!)){
                updateExpressionAndResultData(dot)
            }
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
            button0.setOnClickListener { appendNumberValue("0") }
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
