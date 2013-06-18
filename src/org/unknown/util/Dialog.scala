package org.unknown.util

import android.app.AlertDialog
import android.content.DialogInterface
import android.app.Activity

object Dialog {
	def errorDialog(activity:Activity, msg:String){
		val builder = new AlertDialog.Builder(activity)
		builder.setMessage(msg)
		builder.setCancelable(true)
		builder.setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			def onClick(dialog:DialogInterface, witch:Int) {
				dialog.cancel()
			}
		})
		builder.show()
	}
}