package com.mccham.printtest.views.fragments
/**cma - insert*/

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mccham.printtest.common.CommonFunction
import com.mccham.printtest.databinding.FragmentPrintTestBinding
import com.mccham.printtest.service.print_services.PrintService
import com.mccham.printtest.service.print_services.ZPLConverter
import kotlinx.coroutines.launch
import java.io.InputStream

class PrintTestFragment : Fragment() {
    companion object
    {
        fun newInstance() : Fragment =  PrintTestFragment()
    }

    private lateinit var _binding : FragmentPrintTestBinding
    private var uri : Uri ?= null
    private val binding get() =  _binding!!
    private var result_bitmap : Bitmap ?= null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrintTestBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ZPLConverter.initializeHashMap()
        onClickListener()
    }

    private fun onClickListener()
    {
        with(binding)
        {
            developerImg.setOnClickListener {
                if(ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    imagePick()
                }else{
                    requestPermission()
                }
            }
            btnPrint.setOnClickListener {
                lifecycleScope.launch {

//                    var bitmap = (developerImg.drawable as BitmapDrawable).bitmap

                    if (result_bitmap == null)
                    {
                        Toast.makeText(requireContext(), "Image is Empty!", Toast.LENGTH_SHORT).show()
                    }else{
                        var zplcode = ZPLConverter.convertFromImage(result_bitmap,true)
                        zplcode
                        PrintService.print(requireContext(), null,"SSB-ZQ320-01", "ZPL", text = zplcode )

                    }
                }
            }
        }
    }
    private fun requestPermission()
    {
        try {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),101)
        }catch (ex : Exception)
        {
            Toast.makeText(requireContext(), ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode)
        {
            101 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(requireContext(), "Permission Denied!", Toast.LENGTH_SHORT).show()
                }else{
                    imagePick()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun imagePick()
    {
        try {
            var intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Title of Choose"),100)
        }catch (ex : Exception)
        {
            Toast.makeText(requireContext(), ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null)
        {
            uri = data.data
            var inputStr : InputStream = uri?.let { requireActivity().contentResolver.openInputStream(it) }!!
            var bitmap = BitmapFactory.decodeStream(inputStr)



            var result = CommonFunction.resizeImage(bitmap,250)
            result

            result_bitmap = result!!
            binding.developerImg.setImageBitmap(result)


        }
        super.onActivityResult(requestCode, resultCode, data)

    }
}