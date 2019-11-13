package com.denis.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.denis.calculator.adapters.ViewPagerAdapter
import com.denis.calculator.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ExpressionResultViewModel

    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initializeViewModel()

        viewPager = binding.fragmentsLayout
        pagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
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

    private fun initializeViewModel(){
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
    }
}
