package com.denis.calculator


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.denis.calculator.databinding.FragmentAdvancedKeyboardBinding
import com.denis.calculator.services.ExpressionService

class AdvancedKeyboardFragment : Fragment() {

    private lateinit var binding: FragmentAdvancedKeyboardBinding
    private lateinit var viewModel: ExpressionResultViewModel
    private lateinit var expressionService: ExpressionService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_advanced_keyboard,
            container,
            false
        )
        binding.buttonDefaultFragment.setOnClickListener { switchToDefaultKeyboard() }

        viewModel = activity?.run {
            ViewModelProviders.of(this)[ExpressionResultViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        expressionService = ExpressionService(viewModel)
        setFunctionsListeners()
        setConstantsListeners()
        setBracketListeners()
        setOperatorListeners()

        return binding.root
    }

    private fun setFunctionsListeners(){
        binding.apply {
            buttonSin.setOnClickListener { expressionService.addFunction("sin(") }
            buttonCos.setOnClickListener { expressionService.addFunction("cos(") }
            buttonTan.setOnClickListener { expressionService.addFunction("tan(") }
            buttonLn.setOnClickListener  { expressionService.addFunction("ln(") }
            buttonLog.setOnClickListener { expressionService.addFunction("lg(") }
        }
    }
    private fun setConstantsListeners(){
        binding.apply {
            buttonPi.setOnClickListener  { expressionService.addNumber("π") }
            buttonExp.setOnClickListener { expressionService.addNumber("e") }
        }
    }
    private fun setBracketListeners(){
        binding.apply {
            buttonLeftBracket.setOnClickListener  { expressionService.addFunction("(") }
            buttonRightBracket.setOnClickListener { expressionService.addFunction(")") }
        }
    }
    private fun setOperatorListeners(){
        binding.apply {
            buttonPow.setOnClickListener       { expressionService.addOperator("^") }
            buttonFactorial.setOnClickListener { expressionService.addOperator("!") }
        }
    }

    private fun switchToDefaultKeyboard(){
        val defaultKeyboard = DefaultKeyboardFragment()
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.replace(R.id.fragmentsLayout, defaultKeyboard)
        transaction?.commit()
    }
}
