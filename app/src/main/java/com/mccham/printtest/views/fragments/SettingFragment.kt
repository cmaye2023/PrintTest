package com.mccham.printtest.views.fragments
/**cma - insert*/

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mccham.printtest.databinding.FragmentHomeBinding
import com.mccham.printtest.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {
    companion object
    {
        fun newInstance() : Fragment =  SettingFragment()
    }

    private lateinit var _binding : FragmentSettingBinding
    private val binding get() =  _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater,container,false)
        return binding.root
    }
}