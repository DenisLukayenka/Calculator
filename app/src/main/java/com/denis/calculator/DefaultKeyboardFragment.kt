package com.denis.calculator

import android.os.Bundle
import android.renderscript.ScriptGroup
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.denis.calculator.databinding.DefaultKeyboardFragmentBinding
import kotlinx.android.synthetic.main.default_keyboard_fragment.*


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

        binding.buttonFragmentAdvanced.setOnClickListener{ switchToAdvancedKeyboard() }
        viewModel = activity?.run {
            ViewModelProviders.of(this)[SharedViewModel::class.java]
        } ?: throw Exception("Invalid Activity")

        setNumericListeners()

        return binding.root
    }

    private fun switchToAdvancedKeyboard(){
        val advancedKeyboard = AdvancedKeyboardFragment()
        val transaction = fragmentManager?.beginTransaction()
        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.replace(R.id.fragmentsLayout, advancedKeyboard)
        transaction?.commit()
    }

    private fun inputNumericValue(view: View){
        when(view.id){
            R.id.button_0 -> if(!viewModel.command.value.equals("0")) viewModel.addNewData("0")
            R.id.button_1 -> viewModel.addNewData("1")
            R.id.button_2 -> viewModel.addNewData("2")
            R.id.button_3 -> viewModel.addNewData("3")
            R.id.button_4 -> viewModel.addNewData("4")
            R.id.button_5 -> viewModel.addNewData("5")
            R.id.button_6 -> viewModel.addNewData("6")
            R.id.button_7 -> viewModel.addNewData("7")
            R.id.button_8 -> viewModel.addNewData("8")
            R.id.button_9 -> viewModel.addNewData("9")

            else -> viewModel.addNewData("")
        }
    }

    private fun setNumericListeners(){
        binding.apply {
            val defaultButtons: List<View> = listOf(
                button0,
                button1,
                button2,
                button3,
                button4,
                button5,
                button6,
                button7,
                button8,
                button9,
                button0
            )

            for(view in defaultButtons){
                view.setOnClickListener { inputNumericValue(view) }
            }
        }
    }
}
