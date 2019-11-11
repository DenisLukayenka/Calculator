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

/**
 * A simple [Fragment] subclass.
 */
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

        return binding.root
    }

    private fun switchToDefaultKeyboard(){
        val defaultKeyboard = DefaultKeyboardFragment()
        val transaction = fragmentManager?.beginTransaction()

        transaction?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        transaction?.replace(R.id.fragmentsLayout, defaultKeyboard)
        transaction?.commit()
    }
}
