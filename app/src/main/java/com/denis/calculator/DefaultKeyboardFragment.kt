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
import com.denis.calculator.services.ExpressionService

class DefaultKeyboardFragment : Fragment() {

    private lateinit var binding: DefaultKeyboardFragmentBinding
    private lateinit var viewModel: ExpressionResultViewModel
    private lateinit var expressionService: ExpressionService

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
            ViewModelProviders.of(this)[ExpressionResultViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        expressionService = ExpressionService(viewModel)

        setNumericListeners()
        setOperatorListeners()
        setAdvancedButtonsListeners()

        return binding.root
    }

    private fun setNumericListeners(){
        binding.apply {
            button0.setOnClickListener { expressionService.addNumber("0") }
            button1.setOnClickListener { expressionService.addNumber("1") }
            button2.setOnClickListener { expressionService.addNumber("2") }
            button3.setOnClickListener { expressionService.addNumber("3") }
            button4.setOnClickListener { expressionService.addNumber("4") }
            button5.setOnClickListener { expressionService.addNumber("5") }
            button6.setOnClickListener { expressionService.addNumber("6") }
            button7.setOnClickListener { expressionService.addNumber("7") }
            button8.setOnClickListener { expressionService.addNumber("8") }
            button9.setOnClickListener { expressionService.addNumber("9") }
            buttonDot.setOnClickListener { expressionService.addNumber(".") }
        }
    }
    private fun setOperatorListeners(){
        binding.apply {
            buttonMinus.setOnClickListener    { expressionService.addOperator("-") }
            buttonMultiply.setOnClickListener { expressionService.addOperator("*") }
            buttonDivide.setOnClickListener   { expressionService.addOperator("/") }
            buttonModulo.setOnClickListener   { expressionService.addOperator("%") }
            buttonPlus.setOnClickListener     { expressionService.addOperator("+") }
        }
    }
    private fun setAdvancedButtonsListeners(){
        binding.apply {
            buttonFragmentAdvanced.setOnClickListener   { switchToAdvancedKeyboard() }
            buttonResult.setOnClickListener             { expressionService.calculateFinalResult() }
            buttonRemove.setOnClickListener             { expressionService.removeLastSymbol() }
            buttonClear.setOnClickListener              { expressionService.clearExpression() }
        }
    }

    private fun switchToAdvancedKeyboard(){
        val advancedKeyboard = AdvancedKeyboardFragment()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.replace(R.id.fragmentsLayout, advancedKeyboard)
        transaction?.commit()
    }
}
