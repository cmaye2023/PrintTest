package com.mccham.printtest.views.fragments
/**cma - insert*/

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mccham.printtest.databinding.FragmentDashboardBinding
import com.mccham.printtest.databinding.FragmentHomeBinding

class DashboardFragment : Fragment() {
    companion object
    {
        fun newInstance() : Fragment =  DashboardFragment()
    }

    private lateinit var _binding : FragmentDashboardBinding

    private val binding get() =  _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater,container,false)
        return binding.root
    }
}