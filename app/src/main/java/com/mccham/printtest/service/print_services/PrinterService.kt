package com.mccham.printtest.service.print_services
/**cma - created 22-07-2022*/
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.graphics.Bitmap
import com.mccham.printtest.R
import com.mccham.printtest.common.PrintText
import com.rt.printerlibrary.bean.BluetoothEdrConfigBean
import com.rt.printerlibrary.cmd.EscFactory
import com.rt.printerlibrary.enumerate.BmpPrintMode
import com.rt.printerlibrary.enumerate.ConnectStateEnum
import com.rt.printerlibrary.factory.cmd.CmdFactory
import com.rt.printerlibrary.factory.connect.BluetoothFactory
import com.rt.printerlibrary.factory.connect.PIFactory
import com.rt.printerlibrary.factory.printer.UniversalPrinterFactory
import com.rt.printerlibrary.printer.RTPrinter
import com.rt.printerlibrary.setting.BitmapSetting
import com.rt.printerlibrary.setting.CommonSetting
import com.rt.printerlibrary.utils.BitmapConvertUtil
import com.rt.printerlibrary.utils.ConnectListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Use Object for Singleton Pattern
 */
object PrintService
{
    suspend fun print(context : Context, bitmap : Bitmap? = null, deviceName : String, printLanguage : String, text : String? = null) : String
    {
        return withContext(Dispatchers.IO) {
            setupBluetoothDevice(context = context, bitmap = bitmap, deviceName = deviceName, printLanguage = printLanguage, text = text)
        }
    }

    //Validate bluetooth to match with saved printer name
    private suspend fun setupBluetoothDevice(context : Context, bitmap : Bitmap?, deviceName : String, printLanguage : String, text : String?) : String
    {
        var printResult : String = ""
        for (bluetoothDevice : BluetoothDevice in (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter.bondedDevices)
        {
            if (deviceName == bluetoothDevice.name)
            {
                val connectionResult = connectBluetooth(BluetoothEdrConfigBean(bluetoothDevice))

                when (connectionResult[0].result)
                {
                    PrintText.NOT_CONNECT_PRINTER -> printResult =  context.getString(R.string.connect_printer_fail)

                    PrintText.CONNECTION_ERROR ->printResult =  context.getString(R.string.bluetooth_connection_error)

                    else -> when (printLanguage)
                    {
                        PrintText.PRINTER_LANGUAGE_ESC_Image -> printResult =  printESCImage(context, bitmap !!, connectionResult[0].printer)
                        PrintText.PRINTER_LANGUAGE_ZPL -> printResult =  printZPLText(context, text, connectionResult[0].printer)
                    }
                }
            }

        }
        return printResult
//        return context.getString(R.string.no_such_printer, deviceName)
    }

    //Connect the printer
    private suspend fun connectBluetooth(bluetoothEdrConfigBean : BluetoothEdrConfigBean) : MutableList<PrintModel>
    {
        val piFactory : PIFactory = BluetoothFactory()
        val printerInterface = piFactory.create()
        val printer = UniversalPrinterFactory().create()
        printerInterface.configObject = bluetoothEdrConfigBean
        printer !!.setPrinterInterface(printerInterface)    //Printer Listener
        printer.setConnectListener(object : ConnectListener
        {
            override fun onPrinterConnected(p0 : Any?)
            {
            }

            override fun onPrinterDisconnect(p0 : Any?)
            {
            }

            override fun onPrinterWritecompletion(p0 : Any?)
            {
                Thread.sleep(500)
                printer.disConnect()
            }
        })

//        printer.disConnect()
        var connectionStatus = try
        {
            printer.connect(bluetoothEdrConfigBean)
            withTimeoutOrNull(5000) { //6000
                while (printer.connectState == ConnectStateEnum.NoConnect)
                {
                    delay(500)
                    printer.connectState
                }
            }.toString()
        }
        catch (e : Exception)
        {
            PrintText.CONNECTION_ERROR
        }

        if (printer.connectState == ConnectStateEnum.NoConnect)
        {
            connectionStatus = PrintText.NOT_CONNECT_PRINTER
        }
        return mutableListOf(PrintModel(connectionStatus, printer))
    }

    private fun printESCImage(context : Context, bitmap : Bitmap, printer : RTPrinter<Any>?) : String
    {
        try
        {
            /**
             * Prepare bitmap to convert printable
             * Uses 72 for 3 inch printing width
             */
            val mBitmap = BitmapConvertUtil.decodeSampledBitmapFromBitmap(bitmap, 72 * 8, 4000)      //Init print function such as variables
            val cmdFactory : CmdFactory = EscFactory()
            val cmd = cmdFactory.create()
            val commonSetting = CommonSetting()
            val bitmapSetting = BitmapSetting()      //Init printer
            cmd.append(cmd.headerCmd)      //Line spacing uses 30 in below commend
            cmd.append(cmd.getCommonSettingCmd(commonSetting))
            /**
             * MODE_MULTI_COLOR - Suitable for multi-level grayscale printing
             * MODE_SINGLE_COLOR - Suitable for printing black and white paper
             */
            bitmapSetting.bmpPrintMode = BmpPrintMode.MODE_SINGLE_COLOR
            /**b
             * Print width limiter
             * uses 80 mm the 3 inch paper width
             */
            bitmapSetting.bimtapLimitWidth = 80 * 8      //Convert bitmap into printable
            cmd.append(cmd.getBitmapCmd(bitmapSetting, mBitmap))      //Line Feed
            cmd.append(cmd.lfcrCmd)      //Write image
            printer?.writeMsg(cmd.appendCmds)

            return context.getString(R.string.print_successful)
        }
        catch (e : java.lang.Exception)
        {
            return context.getString(R.string.printing_error)
        }
    }


    fun printZPLText(context : Context, text : String?, printer : RTPrinter<Any>?) : String
    {
        return try
        {
            val textByteArray = text !!.toByteArray()
            printer?.writeMsg(textByteArray)

            context.getString(R.string.print_successful)
        }
        catch (e : java.lang.Exception)
        {
            context.getString(R.string.printing_error)
        }
    }
}

data class PrintModel(var result : String, var printer : RTPrinter<Any>?)