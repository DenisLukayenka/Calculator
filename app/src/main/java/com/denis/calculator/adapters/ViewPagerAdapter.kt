package com.denis.calculator.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.denis.calculator.AdvancedKeyboardFragment
import com.denis.calculator.DefaultKeyboardFragment
import java.lang.Exception

class ViewPagerAdapter(fm: FragmentManager) :
    FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> DefaultKeyboardFragment.newInstance()
            0 -> AdvancedKeyboardFragment.newInstance()
            else -> {
                throw Exception()
            }
        }
    }
}
