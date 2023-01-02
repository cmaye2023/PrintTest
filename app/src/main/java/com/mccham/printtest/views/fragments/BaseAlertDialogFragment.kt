package com.mccham.printtest.views.fragments
/**cma - insert*/

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class BaseAlertDialogFragment(var title : String, val message : String, private val positiveButton : String, private val negativeButton : String,var icon : Int?, private val onItemClicked : (DialogFragment, String) -> Unit) : DialogFragment()
{
	override fun onCreateDialog(savedInstanceState : Bundle?) : Dialog
	{
		return activity?.let {
			val builder = AlertDialog.Builder(it)
				builder.setTitle(title)
				builder.setMessage(message)
				builder.setIcon(icon!!)
				.setPositiveButton(positiveButton, DialogInterface.OnClickListener { dialog, id -> onItemClicked(this, "P") })
				.setNegativeButton(negativeButton, DialogInterface.OnClickListener { dialog, id -> onItemClicked(this, "N") })
            builder.setCancelable(true)
			builder.create()
		} ?: throw IllegalStateException("Activity cannot be null")
	}
}