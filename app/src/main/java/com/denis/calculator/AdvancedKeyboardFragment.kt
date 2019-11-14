package com.denis.calculator


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
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

        viewModel = activity?.run {
            ViewModelProviders.of(this)[ExpressionResultViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        viewModel.isDegSelected.observe(this, Observer<Boolean> { item ->
            if(item){
                activateButton(binding.buttonDeg)
                deactivateButton(binding.buttonRadical)
            } else {
                activateButton(binding.buttonRadical)
                deactivateButton(binding.buttonDeg)
            }
        })

        expressionService = ExpressionService(viewModel)
        setFunctionsListeners()
        setConstantsListeners()
        setOperatorListeners()
        setActionListeners()

        return binding.root
    }


    private fun setFunctionsListeners(){
        binding.apply {
            buttonSin.setOnClickListener { expressionService.addFunction("sin(") }
            buttonCos.setOnClickListener { expressionService.addFunction("cos(") }
            buttonTan.setOnClickListener { expressionService.addFunction("tan(") }
            buttonLn.setOnClickListener  { expressionService.addFunction("ln(") }
            buttonLog.setOnClickListener { expressionService.addFunction("lg(") }
            buttonSqrt.setOnClickListener{ expressionService.addFunction("√(") }

            buttonLeftBracket.setOnClickListener  { expressionService.addFunction("(") }
            buttonRightBracket.setOnClickListener { expressionService.addFunction(")") }
        }
    }
    private fun setConstantsListeners(){
        binding.apply {
            buttonPi.setOnClickListener  { expressionService.addNumber("π") }
            buttonExp.setOnClickListener { expressionService.addNumber("e") }
        }
    }
    private fun setOperatorListeners(){
        binding.apply {
            buttonPow.setOnClickListener       { expressionService.addOperator("^") }
            buttonFactorial.setOnClickListener { expressionService.addOperator("!") }
        }
    }
    private fun setActionListeners(){
        binding.apply {
            buttonRadical.setOnClickListener { expressionService.onSelectedRad() }
            buttonDeg.setOnClickListener { expressionService.onSelectedDeg() }
            buttonDefaultFragment.setOnClickListener { switchToDefaultKeyboard() }
        }
    }

    private fun switchToDefaultKeyboard(){
        activity!!.findViewById<ViewPager>(R.id.fragmentsLayout).setCurrentItem(0, true)
    }

    private fun activateButton(button: View){
        button.isSelected = true
        if(button is Button){
            button.setTextColor(resources.getColor(R.color.keyboardBackgroundColor))
        }
    }

    private fun deactivateButton(button: View){
        button.isSelected = false
        if(button is Button){
            button.setTextColor(resources.getColor(R.color.liteBlue))
        }
    }

    companion object {

        // Method for creating new instances of the fragment
        fun newInstance(): AdvancedKeyboardFragment {

            // Store the movie data in a Bundle object
            val args = Bundle()
            // args.putString(MovieHelper.KEY_TITLE, movie.title)

            // Create a new MovieFragment and set the Bundle as the arguments
            // to be retrieved and displayed when the view is created
            val fragment = AdvancedKeyboardFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
