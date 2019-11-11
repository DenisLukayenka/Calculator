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


class DefaultKeyboardFragment : Fragment() {
    private lateinit var binding: DefaultKeyboardFragmentBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
            val expression = ExpressionBuilder(viewModel.command.value!!.toLowerCase()).build()
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
        val expressionCommand = viewModel.command.value!!

        if(expressionCommand.isNotEmpty()){
            viewModel.command.value = expressionCommand.substring(0, expressionCommand.length - 1)
        }

        viewModel.result.value = ""
    }

    private fun clearExpressionCommandAndResult(){
        viewModel.result.value = ""
        viewModel.command.value = ""
    }

    private fun appendExpressionCommand(command: String, canClear: Boolean){
        if(viewModel.result.value!!.isNotEmpty()){
            viewModel.command.value = ""
        }

        if(canClear){
            viewModel.updateResultValue("")
            viewModel.addNewData(command)
        } else {
            var actualResultValue = viewModel.result.value!!
            if(actualResultValue.isNotEmpty()){
                actualResultValue = actualResultValue.substring(2, actualResultValue.length)
            }

            viewModel.addNewData(actualResultValue)
            viewModel.addNewData(command)
            viewModel.updateResultValue("")
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

    private fun setNumericListeners(){
        binding.apply {
            button0.setOnClickListener { appendExpressionCommand("0", true) }
            button1.setOnClickListener { appendExpressionCommand("1", true) }
            button2.setOnClickListener { appendExpressionCommand("2", true) }
            button3.setOnClickListener { appendExpressionCommand("3", true) }
            button4.setOnClickListener { appendExpressionCommand("4", true) }
            button5.setOnClickListener { appendExpressionCommand("5", true) }
            button6.setOnClickListener { appendExpressionCommand("6", true) }
            button7.setOnClickListener { appendExpressionCommand("7", true) }
            button8.setOnClickListener { appendExpressionCommand("8", true) }
            button9.setOnClickListener { appendExpressionCommand("9", true) }
            buttonDot.setOnClickListener { appendExpressionCommand(".", true) }
        }
    }
    private fun setCommandListeners(){
        binding.apply {
            buttonMinus.setOnClickListener { appendExpressionCommand("-", false) }
            buttonMultiply.setOnClickListener { appendExpressionCommand("*", false) }
            buttonDivide.setOnClickListener { appendExpressionCommand("/", false) }
            buttonModulo.setOnClickListener { appendExpressionCommand("%", false) }
            buttonPlus.setOnClickListener { appendExpressionCommand("+", false) }
        }
    }
}
