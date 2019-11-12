package com.denis.calculator

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.denis.calculator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ExpressionResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProviders.of(this)[ExpressionResultViewModel::class.java]
        viewModel.expression.observe(this, Observer<String> { item ->
            binding.actualCommandText.text = item
        })
        viewModel.resultValue.observe(this, Observer<String> { item ->
            binding.commandResultText.text = item
        })
        viewModel.isResultOnFocus.observe(this, Observer<Boolean> { item ->
            onResultFocusChanged(item)
        })

        val defaultKeyboard = DefaultKeyboardFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.fragmentsLayout, defaultKeyboard)
        transaction.commit()
    }

    private fun onResultFocusChanged(isResultOnFocus: Boolean){
        if(isResultOnFocus){
            binding.actualCommandText.setTextAppearance(R.style.expression_not_focused)
            binding.commandResultText.setTextAppearance(R.style.result_on_focus)
            binding.textViewEqualText.setTextAppearance(R.style.result_on_focus)

        } else{
            binding.actualCommandText.setTextAppearance(R.style.expression_on_focus)
            binding.commandResultText.setTextAppearance(R.style.result_not_focused)
            binding.textViewEqualText.setTextAppearance(R.style.result_not_focused)
        }
    }
}
