package com.denis.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.denis.calculator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProviders.of(this)[SharedViewModel::class.java]
        viewModel.expression.observe(this, Observer<String> { item ->
            binding.actualCommandText.text = item
        })
        viewModel.resultValue.observe(this, Observer<String> { item ->
            binding.commandResultText.text = item
        })

        val defaultKeyboard = DefaultKeyboardFragment()
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.fragmentsLayout, defaultKeyboard)
        transaction.commit()
    }
}
