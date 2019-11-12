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

class AdvancedKeyboardFragment : Fragment() {

    private lateinit var binding: FragmentAdvancedKeyboardBinding
    private lateinit var viewModel: SharedViewModel

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
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")


        setFunctionsListeners()
        setConstantsListeners()
        setBracketListeners()
        setOperatorListeners()

        return binding.root
    }

    private fun functionListener(function: String){
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
    private fun constantListener(constant: String){
        viewModel.clearExpressionData()
        val regex = Regex("\\W0\$")
        val floatNumberRegex = Regex("\\d+(\\.)\\d*\$")

        val expression = viewModel.expression.value!!

        if((regex.containsMatchIn(expression) && !floatNumberRegex.containsMatchIn(expression))
            || expression == "0"){
            viewModel.removeLastExpressionSymbol()
        }

        updateExpressionAndResultData(constant)
        viewModel.isCommandActive = false
    }
    private fun rightBracketListener(rightBracket: String){
        viewModel.clearExpressionData()

        if(viewModel.bracketsToClose > 0){
            updateExpressionAndResultData(rightBracket)
            viewModel.bracketsToClose--
        }
    }

    private fun operatorListener(operator: String){
        viewModel.clearExpressionData()

        if(viewModel.isCommandActive){
            val expression = viewModel.expression.value!!
            if(expression.isNotEmpty()){
                viewModel.removeLastExpressionSymbol()
            }
        }

        viewModel.addExpressionData(viewModel.getResultValue())
        updateExpressionAndResultData(operator)
        viewModel.isCommandActive = true
    }

    private fun setFunctionsListeners(){
        binding.apply {
            buttonSin.setOnClickListener { functionListener("sin(") }
            buttonCos.setOnClickListener { functionListener("cos(") }
            buttonTan.setOnClickListener { functionListener("tan(") }
            buttonLn.setOnClickListener { functionListener("ln(") }
            buttonLog.setOnClickListener { functionListener("lg(") }
        }
    }
    private fun setConstantsListeners(){
        binding.apply {
            buttonPi.setOnClickListener { constantListener("π") }
            buttonExp.setOnClickListener { constantListener("e") }
        }
    }
    private fun setBracketListeners(){
        binding.apply {
            buttonLeftBracket.setOnClickListener { functionListener("(") }
            buttonRightBracket.setOnClickListener { rightBracketListener(")") }
        }
    }
    private fun setOperatorListeners(){
        binding.apply {
            buttonPow.setOnClickListener { operatorListener("^") }
            buttonFactorial.setOnClickListener { operatorListener("!") }
        }
    }

    private fun switchToDefaultKeyboard(){
        val defaultKeyboard = DefaultKeyboardFragment()
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.replace(R.id.fragmentsLayout, defaultKeyboard)
        transaction?.commit()
    }

    private fun updateExpressionAndResultData(data: String){
        viewModel.updateResultValue("")
        viewModel.addExpressionData(data)
    }
}
