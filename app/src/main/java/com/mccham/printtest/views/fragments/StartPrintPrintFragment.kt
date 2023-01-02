package com.mccham.printtest.views.fragments
/**cma - insert*/


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mccham.printtest.databinding.FragmentStartPrintConnectBinding
import com.mccham.printtest.databinding.FragmentStartPrintPrintBinding

class StartPrintPrintFragment : Fragment() {
    companion object
    {
        fun newInstance() : Fragment =  StartPrintPrintFragment()
    }

    private lateinit var _binding : FragmentStartPrintPrintBinding
    private val binding get() =  _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartPrintPrintBinding.inflate(inflater,container,false)
        return binding.root
    }
}