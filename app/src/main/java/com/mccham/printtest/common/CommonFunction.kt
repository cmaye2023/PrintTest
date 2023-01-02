package com.mccham.printtest.common

import android.graphics.Bitmap
import java.util.*


class CommonFunction {
    companion object
    {



        /**split text*/
        fun splitText(textStr: String,count : Int) : MutableList<String>
        {
            var stringList : MutableList<String> = mutableListOf()
            var tempStr = ""

            if (textStr.isNullOrEmpty())
                stringList
            else if (textStr.length < count)
                stringList.add(textStr)
            else{
                for (element in textStr)
                {
                    tempStr += element
                    if (tempStr.length == count)
                    {
                        stringList.add(tempStr)
                        tempStr = ""
                    }
                }
                //for char < count
                if (!tempStr.isNullOrEmpty())
                    stringList.add(tempStr)
            }
            return stringList
        }

        fun resizeImage(bitmap : Bitmap,size : Int) : Bitmap
        {
            var resizeImage = Bitmap.createScaledBitmap(bitmap,size,size,true)
            return resizeImage
        }
    }
}