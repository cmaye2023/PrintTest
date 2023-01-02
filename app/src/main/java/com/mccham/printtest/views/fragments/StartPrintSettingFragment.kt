package com.mccham.printtest.views.fragments
/**cma - insert*/


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mccham.printtest.databinding.FragmentStartPrintSettingBinding

class StartPrintSettingFragment : Fragment() {
    companion object
    {
        fun newInstance() : Fragment =  StartPrintSettingFragment()
    }

    private lateinit var _binding : FragmentStartPrintSettingBinding
    private val binding get() =  _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartPrintSettingBinding.inflate(inflater,container,false)
        return binding.root
    }
}