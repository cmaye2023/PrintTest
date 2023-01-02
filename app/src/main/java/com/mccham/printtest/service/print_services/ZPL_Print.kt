package com.mccham.printtest.service.print_services


/**cma - created 22-07-2022*/

object ZPL_Print
{
    private const val strikeThroughText = "StrikeThroughText"

    private const val paperWidth_3_inches = 580 // for 3 - inches
    private const val paperWidth_4_inches = 790 // for 4 - inches


    private const val right = "R"
    private const val left = "L"
    private const val center = "C"

    private const val BLACK = "B"
    private const val WHITE = "W"

    private const val normalSize = 0
    private const val headerSize = 30
    private const val header1Size = 27
    private const val header2Size = 24


    private var paperLength = 0

    private var positionY = 0
    private var result_text = ""


    fun printTestPage(imageZplCode : String) : String
    {
        result_text = ""
        result_text += imageZplCode
        dataLength(50)
        result_text += getFont("T",30)
        result_text += getDrawText(580, left,250, paperLength,"ZEBRA")

        dataLength(50)
        result_text += getFont("T",26)
        result_text += getDrawText(580, left,250, paperLength,"Technologies")

        dataLength(200)
        return generateFinalString(result_text,"KAI000.FNT", paperLength)

    }












    fun printTest() : String
    {
        paperLength = 0
        positionY = 0
        dataLength(35)
        result_text += getFont("T",26)
        result_text += getDrawText(580, left,195, paperLength,"Zebra Print")
        dataLength(100)

        return generateFinalString(result_text, "KAI000.FNT", paperLength)

    }


    fun dataLength(len : Int)
    {
        positionY += len
        paperLength += len
    }

    //For Void Vocher
    fun getStrikeTest(positionX: Int,positionY: Int) : String
    {
        return "^FO$positionX,${positionY -20}^GB${paperWidth_3_inches-30},0,2^FS\n"

    }


    fun getFont(fontName : String,fontWidth : Int,fontHeight : Int = 0) : String
    {
        return if (fontHeight ==  0)
            "^CF$fontName,$fontWidth"
        else "^CF$fontName,$fontWidth,$fontHeight"
    }

    fun getDrawText(maximumWidth : Int,alignment : String,positionX : Int,positionY: Int,text : String) : String
    {
        return "^FB$maximumWidth,1,0,$alignment,0 ^FO$positionX,$positionY^FD$text^FS"
    }


    fun getUnderlineText(positionX: Int,positionY: Int,underlineWidth : Int,high: Int,thickness : Int, color : String)  : String
    {
        return "^FO$positionX,$positionY^GB$underlineWidth,$high,$thickness,$color^FS"
    }


    fun generateFinalString(text : String, font : String, high : Int) : String
    {
        return "^XA^LH0,0^LL$high^POI\n$text^XZ"
    }

    private fun drawText(positionX : Int, positionY : Int, alignment : String, size : Int, text : String, textType : String = "") : String
    {
        var text = text
        when (size)
        {
            normalSize ->
            {
                text = "^FT$positionX,$positionY$alignment^FD$text^FS\n"
            }

            else ->
            {
                var alignment = alignment
                when (alignment)
                {
                    left -> alignment = "L"
                    right -> alignment = "R"
                    center -> alignment = center
                }
                alignment = "^FB$paperWidth_3_inches,1,0,$alignment,0"

                text = "^FT0,0^FD^FS^FO0,$positionY$alignment^A0N$size,$size^FD$text^FS\n"
            }
        }


        //For Void Vocher
        if (textType == strikeThroughText)
        {
            text += "^FO$positionX,${positionY - 7}^GB$paperWidth_3_inches,0,1^FS\n"
        }

        return text
    }
}

//^AN - is i like font