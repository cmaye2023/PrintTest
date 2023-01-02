package com.mccham.printtest.views.fragments
/**cma - insert*/


import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mccham.printtest.R
import com.mccham.printtest.common.CommonFunction
import com.mccham.printtest.common.PrintText
import com.mccham.printtest.databinding.FragmentStartPrintConnectBinding
import com.mccham.printtest.service.print_services.PrintModel
import com.mccham.printtest.service.print_services.PrintService
import com.mccham.printtest.service.print_services.ZPLConverter
import com.mccham.printtest.service.print_services.ZPL_Print
import com.rt.printerlibrary.bean.BluetoothEdrConfigBean
import com.rt.printerlibrary.enumerate.ConnectStateEnum
import com.rt.printerlibrary.factory.connect.BluetoothFactory
import com.rt.printerlibrary.factory.connect.PIFactory
import com.rt.printerlibrary.factory.printer.UniversalPrinterFactory
import com.rt.printerlibrary.printer.RTPrinter
import com.rt.printerlibrary.utils.ConnectListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class StartPrintConnectFragment : Fragment() {
    companion object
    {
        fun newInstance() : Fragment =  StartPrintConnectFragment()
    }

    private var pairPrinter: RTPrinter<Any>? = null
    private var connectionResult: MutableList<PrintModel> ?= null
    private var pairDeviceList  = mutableListOf<String>("")
    private lateinit var bluetoothArrayAdapter : ArrayAdapter<String>
    private var printer:String?=""

    private lateinit var _binding : FragmentStartPrintConnectBinding
    private val binding get() =  _binding!!
    private lateinit var bluetoothManager: BluetoothManager
    private var log : Int = 0
    private var handler = Handler()
    private var connectionStatus :Boolean  = false
    private var portType : String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartPrintConnectBinding.inflate(inflater,container,false)
        setUpBluetoothDevice()
        onClickListener()
        return binding.root
    }


    private fun setUpBluetoothDevice()
    {
        with(binding)
        {
            try {
                pairDeviceList.clear()
                pairDeviceList.add("")
                bluetoothManager = requireContext().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//                bluetoothManager.adapter.bondedDevices
//                ).adapter.bondedDevices
                for (bt : BluetoothDevice in bluetoothManager.adapter.bondedDevices)
                {
                    pairDeviceList.add(bt.name)
                }
                if(pairDeviceList.size<=1 && pairDeviceList[0].isNullOrEmpty()  )
                {
                    if(!printer.isNullOrEmpty())
                    {
                        pairDeviceList.clear()
                        pairDeviceList.add(printer!!)
                    }
                }

                bluetoothArrayAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_dropdown_item,pairDeviceList)
                bluetoothArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.connectBluetoothSpinner.adapter = bluetoothArrayAdapter
                binding.connectBluetoothSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                    override fun onItemSelected( adapterView: AdapterView<*>?, view: View?, position: Int, p3: Long) {
                        var item = adapterView!!.getItemAtPosition(position).toString()
                        printer = if (item.isNullOrEmpty()) {
                            ""
                        }else item
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        binding.connectBluetoothSpinner.setSelection(0)
                    }
                }

            }catch (ex : Exception)
            {
                Toast.makeText(requireContext(), ex.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun onClickListener()
    {
        with(binding)
        {
            try {
                btnConnect.setOnClickListener {
                    if (connectionStatus)
                    {
                        var connectionStatusDialog = BaseAlertDialogFragment(getString(R.string.user_warning),getString(R.string.device_is_not_pair_printer),"OK","Cancel",R.drawable.ic_baseline_warning){dialog, action ->
                            if (action == "P")
                            {
                                if (pairPrinter != null)
                                {
                                    pairPrinter!!.disConnect()
                                    tvConnectionStatus.text = getString(R.string.un_connect)
                                    tvConnectionStatus.setTextColor(Color.parseColor("#9C2323"))
                                    btnConnect.text = getString(R.string.connect)
                                }
                                dialog.dismiss()
                            }else{
                                dialog.dismiss()
                            }
                        }
                        connectionStatusDialog.isCancelable = false
                        connectionStatusDialog.show(childFragmentManager,"CMA")
                    }else{
                        if (radioGroup1.checkedRadioButtonId == -1)
                        {
                            Toast.makeText(requireContext(), "Choose Port Type !", Toast.LENGTH_SHORT).show()
                        }else if(radioGroup2.checkedRadioButtonId == -1){
                            Toast.makeText(requireContext(), "Choose Print Type !", Toast.LENGTH_SHORT).show()
                        }else if (connectBluetoothSpinner.selectedItem.toString().isNullOrEmpty()){
                            Toast.makeText(requireContext(), "Choose Printer!", Toast.LENGTH_SHORT).show()
                        }else{
                            progressLayout.visibility = View.VISIBLE
                            log = binding.progressBar.progress

                            lifecycleScope.launch {
                                Thread(Runnable {
                                    log +=1
                                    handler.post(Runnable {
                                        progressBar.progress = log
                                    })
                                    try {
                                        Thread.sleep(1000)
                                    }catch (ex : Exception){ex.printStackTrace()}
                                    finally {

                                    }
                                })
                                connectedPrinterDevice()
                            }

                        }
                    }
                }
                btnPrintTest.setOnClickListener {

                    lifecycleScope.launch {
                        var aa = resources.getDrawable(R.mipmap.zebra_logo,null)
                        aa

                        var logo = ResourcesCompat.getDrawable(getResources(), R.mipmap.developer_image, null)
                        logo

                        var zebraLogoBitmap = BitmapFactory.decodeResource(requireContext().resources,R.mipmap.zebra_logo_white)
                        var resizeLogoBitmap = CommonFunction.resizeImage(zebraLogoBitmap,250)
                        var zplCode = ZPLConverter.convertFromImage(resizeLogoBitmap,true)
                        zplCode
                        var printTestCode = ZPL_Print.printTestPage(zplCode!!)
                        PrintService.print(requireContext(), null,"SSB-ZQ320-01", "ZPL", text = printTestCode )
                    }

//                    var zebra_logo  = requireContext().resources.getIdentifier(in)
//                    var printTest = ZPL_Print.printTest()
//                    PrintService.printZPLText(requireContext(),printTest,connectionResult!![0].printer)

                }

                radioGroup1.setOnCheckedChangeListener{view,checkedID ->
                    when(checkedID)
                    {
                        R.id.rbBluetooth ->{
                            tvConnectPortType.text = getString(R.string.BLUETOOTH)
                            portType = getString(R.string.BLUETOOTH)
                        }
                        R.id.rbUSB ->{
                            tvConnectPortType.text = getString(R.string.USB)
                            portType = getString(R.string.USB)
                        }
                        R.id.rbWifi ->{
                            tvConnectPortType.text = getString(R.string.WI_FI)
                            portType = getString(R.string.WI_FI)
                        }
                        else -> false
                    }
                }

            }catch (ex : Exception)
            {
                Toast.makeText(requireContext(), ex.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }
    private suspend fun connectedPrinterDevice() : String
    {
        with(binding)
        {
            var printResult = ""

            for (bluetoothDevice : BluetoothDevice in bluetoothManager.adapter.bondedDevices)
            {
                if (connectBluetoothSpinner.selectedItem.toString() == bluetoothDevice.name)
                {
                    connectionResult = connectBluetooth(BluetoothEdrConfigBean(bluetoothDevice))
                    when (connectionResult!![0].result)
                    {
                        PrintText.NOT_CONNECT_PRINTER -> {
                            progressLayout.visibility = View.GONE
                            printResult =  requireContext().getString(R.string.connect_printer_fail)
                        }

                        PrintText.CONNECTION_ERROR ->
                        {
                            progressLayout.visibility = View.GONE
                            printResult =  requireContext().getString(R.string.bluetooth_connection_error)
                        }

                        else -> {
//                        connectionSuccess()
                            tvConnectionStatus.text = getString(R.string.connect)
                            tvConnectionStatus.setTextColor(Color.GREEN)

                            connectionStatus = true
                            btnConnect.text = getString(R.string.break_option)

                            printResult = requireContext().getString(R.string.print_successful)
                            progressLayout.visibility = View.GONE
                        }
                    }
                }
                else{
                    binding.progressLayout.visibility = View.GONE
                }
            }
            return printResult
        }
    }

    //Connect the printer
    private suspend fun connectBluetooth(bluetoothEdrConfigBean : BluetoothEdrConfigBean) : MutableList<PrintModel>
    {
        val piFactory : PIFactory = BluetoothFactory()
        val printerInterface = piFactory.create()
        pairPrinter = UniversalPrinterFactory().create()
        printerInterface.configObject = bluetoothEdrConfigBean
        pairPrinter !!.setPrinterInterface(printerInterface)    //Printer Listener
        pairPrinter!!.setConnectListener(object : ConnectListener
        {
            override fun onPrinterConnected(p0 : Any?)
            {}

            override fun onPrinterDisconnect(p0 : Any?)
            {}

            override fun onPrinterWritecompletion(p0 : Any?)
            {
                Thread.sleep(500)
//                printer.disConnect()
            }
        })

//        printer.disConnect()
        var connectionStatus = try
        {
            pairPrinter!!.connect(bluetoothEdrConfigBean)
            withTimeoutOrNull(5000) { //6000
                while (pairPrinter!!.connectState == ConnectStateEnum.NoConnect)
                {
                    delay(500)
                    pairPrinter!!.connectState
                }
            }.toString()
        }
        catch (e : Exception)
        {
            PrintText.CONNECTION_ERROR
        }

        if (pairPrinter!!.connectState == ConnectStateEnum.NoConnect)
        {
            connectionStatus = PrintText.NOT_CONNECT_PRINTER
        }
        return mutableListOf(PrintModel(connectionStatus, pairPrinter!!))
    }

    private fun connectionSuccess()
    {
        with(binding)
        {
            try {
                progressLayout.visibility = View.GONE
            }catch (ex :Exception)
            {
                Toast.makeText(requireContext(), ex.toString(), Toast.LENGTH_SHORT).show()
            }
        }

    }
}