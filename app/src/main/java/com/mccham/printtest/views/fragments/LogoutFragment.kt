package com.mccham.printtest.views.fragments
/**cma - insert*/

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mccham.printtest.databinding.FragmentHomeBinding

class LogoutFragment : Fragment() {
    companion object
    {
        fun newInstance() : Fragment =  LogoutFragment()
    }

    private lateinit var _binding : FragmentHomeBinding

    private val binding get() =  _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }
}