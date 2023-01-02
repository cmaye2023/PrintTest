package com.mccham.printtest.views.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mccham.printtest.R
import com.mccham.printtest.databinding.ActivityMainBinding
import com.mccham.printtest.databinding.ActivityStartPrintBinding
import com.mccham.printtest.views.fragments.*
import com.mccham.printtest.views_helper.ViewPager2Adapter

class StartPrintActivity : AppCompatActivity() {


    private val vp2Adapter by lazy {
        ViewPager2Adapter(supportFragmentManager,lifecycle)
    }

    private lateinit var binding : ActivityStartPrintBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartPrintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addFragment()
        vp2Call()
    }

    private fun addFragment()
    {
        with(binding)
        {
            vp2Adapter.addFragment(StartPrintConnectFragment.newInstance())
            vp2Adapter.addFragment(StartPrintPrintFragment.newInstance())
            vp2Adapter.addFragment(StartPrintSettingFragment.newInstance())

            viewPager2.adapter = vp2Adapter
            bottomNavMenu.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        }
    }

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId)
        {
            R.id.id_menu_connect ->
            {
                binding.viewPager2.currentItem =  0
                return@OnNavigationItemSelectedListener true
            }
            R.id.id_menu_print ->
            {
                binding.viewPager2.currentItem =  1
                return@OnNavigationItemSelectedListener true
            }
            R.id.id_menu_setting ->
            {
                binding.viewPager2.currentItem =  2
                return@OnNavigationItemSelectedListener true
            }
            else -> false
        }
    }

    private fun vp2Call()
    {
        with(binding)
        {
            viewPager2.offscreenPageLimit = 3
            viewPager2.isUserInputEnabled = true
            viewPager2.registerOnPageChangeCallback(object  : ViewPager2.OnPageChangeCallback(){
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                }

                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when(position)
                    {
                        0 -> bottomNavMenu.menu.findItem(R.id.id_menu_connect).isChecked = true
                        1 -> bottomNavMenu.menu.findItem(R.id.id_menu_print).isChecked = true
                        2 -> bottomNavMenu.menu.findItem(R.id.id_menu_setting).isChecked = true
                    }
                }

                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                }
            })
        }
    }


    private fun openFragment(fragment : Fragment)
    {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.baseLayout,fragment,"cma")
        transaction.commit()
    }

    private fun setupFragment(fragment : Fragment)
    {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.baseLayout, fragment, "cma")
        transaction.commit()
    }
}