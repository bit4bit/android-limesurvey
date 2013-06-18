package org.bit4bit.androidquickform.el

import java.util.Map
import com.ngohung.form.constant.HConstants
import com.ngohung.form.el.store.HDataStore
import com.ngohung.form.el.validator.ValidationStatus
import com.ngohung.form.util.HStringUtil
import com.ngohung.form.el.HPickerElement
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.Button;
import org.bit4bit.androidquickform.el.HPickerElement2



class HMultiPickerElement(key:String, label:String, hint:String, required:Boolean, options:Array[String], codes:Array[String]) extends HPickerElement2(key, label, hint, required, options, codes) {
	private val TAG = "HMultiPickerElement";
	private var checked:Array[Boolean] = new Array[Boolean](options.size);
	
	override def setValue(v:String) {
	  if(v == null)
		  checked = new Array[Boolean](options.size)
	  super.setValue(v)
	}

	override def onClick(view:View) {
	  if(view.isInstanceOf[Button]) {
	    val pickerBtn:Button = view.asInstanceOf[Button];
	    val builder:AlertDialog.Builder = new AlertDialog.Builder(pickerBtn.getContext());
	    builder.setTitle(this.getHint());
	    builder.setCancelable(true);
		
	    builder.setMultiChoiceItems(options.toArray[java.lang.CharSequence], checked,  new DialogInterface.OnMultiChoiceClickListener {
	    	override def onClick(dialog:DialogInterface, which:Int, isChecked:Boolean): Unit = {
	        checked(which) = isChecked

	        var titleButton:String = "";
	        var valueButton:String = "";
	          Log.d(TAG, "checked size:" + checked.length);
	        for(i <- 0 until checked.length){ 	
	        	if(titleButton != "" && i > 0 && checked(i)){
	        		titleButton += ",";
	        		valueButton += ",";
	        	}
	        	if(checked(i)){
	        	  titleButton += options(i)
	        	  valueButton += codes(i)
	        	}
	        	
	        }
	        setValue(valueButton);
	        Log.d(TAG, "valueMultiChoice:" + valueButton);
	        pickerBtn.setText(titleButton)
	      
	      }
	    } );
	    val alert:AlertDialog = builder.create();
	    alert.show();
	  }
	}
}