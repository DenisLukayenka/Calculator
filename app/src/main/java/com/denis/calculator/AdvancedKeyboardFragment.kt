package com.denis.calculator


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import com.denis.calculator.databinding.FragmentAdvancedKeyboardBinding

/**
 * A simple [Fragment] subclass.
 */
class AdvancedKeyboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentAdvancedKeyboardBinding>(
            inflater,
            R.layout.fragment_advanced_keyboard,
            container,
            false
        )

        binding.buttonDefaultFragment.setOnClickListener { switchToDefaultKeyboard() }

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
