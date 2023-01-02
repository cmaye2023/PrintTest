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
import com.mccham.printtest.R
import com.mccham.printtest.databinding.ActivityMainBinding
import com.mccham.printtest.views.fragments.DashboardFragment
import com.mccham.printtest.views.fragments.HomeFragment
import com.mccham.printtest.views.fragments.PrintTestFragment
import com.mccham.printtest.views.fragments.SettingFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_baseline_triple_lines_menu)
        setContentView(binding.root)

        checkPermission()
        setupFragment(HomeFragment.newInstance())
        onClickListener()
    }


    private fun checkPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        }
        else{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }
    }
    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //granted
        }else{
            //deny
        }
    }
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
            }
        }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                binding.drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> false
        }
    }
    private fun onClickListener()
    {
        with(binding)
        {
            navView.setNavigationItemSelectedListener { menuItem ->
                when(menuItem.itemId)
                {
                    R.id.id_menu_Home ->
                    {
                        supportActionBar!!.show()
                        drawerLayout.closeDrawer(GravityCompat.START)
                        openFragment(HomeFragment.newInstance())
                        return@setNavigationItemSelectedListener true
                    }

                    R.id.id_menu_Dashboard ->
                    {
                        supportActionBar!!.show()
                        drawerLayout.closeDrawer(GravityCompat.START)
                        openFragment(DashboardFragment.newInstance())
                        return@setNavigationItemSelectedListener true
                    }
                    R.id.id_menu_PrintsTest ->
                    {
                        supportActionBar!!.show()
                        drawerLayout.closeDrawer(GravityCompat.START)
                        openFragment(PrintTestFragment.newInstance())
                        return@setNavigationItemSelectedListener true
                    }
                    R.id.id_menu_Settings ->
                    {
                        supportActionBar!!.show()
                        drawerLayout.closeDrawer(GravityCompat.START)
                        openFragment(SettingFragment.newInstance())
                        return@setNavigationItemSelectedListener true
                    }

                    R.id.id_menu_Logout ->
                    {
                        supportActionBar!!.show()
                        drawerLayout.closeDrawer(GravityCompat.START)
                        openFragment(HomeFragment.newInstance())
                        return@setNavigationItemSelectedListener true
                    }
                    else -> false
                }
            }
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