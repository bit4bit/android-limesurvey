package org.bit4bit.androidquickform.el;

import java.util.Map;

import com.ngohung.form.constant.HConstants;
import com.ngohung.form.el.HElementType;
import com.ngohung.form.el.store.HDataStore;
import com.ngohung.form.el.validator.ValidationStatus;
import com.ngohung.form.util.HStringUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class HPickerElement2 extends com.ngohung.form.el.HPickerElement {
	private int selectedIndex = HConstants.NOT_SPECIFIED;
	private String[] optionsCode = null;
	private String[] options;  									// list of option for display
	
	// constructors

	public HPickerElement2(String key, String label, String hint, boolean required, String[] options, String[] codes)
	{
		super(key, label, hint, required , options);
		this.optionsCode = codes;
		this.options = options;
	}
	
	public HPickerElement2(String key, String label, String hint, boolean required, String options[], String[] codes, HDataStore store)
	{
		super(key, label, hint, required, options, HConstants.NOT_SPECIFIED, store);
		this.optionsCode = codes;
		this.options = options;
	}

	
	@Override
	public void setValue(String newString) {
		if(newString == null)
			selectedIndex = HConstants.NOT_SPECIFIED;
		super.setValue(newString);
	}
	
	// trigger the dialog upon clicking of the picker btn
	@Override
	public void onClick(View view) {
		if(view instanceof Button)
		{
			final Button pickerBtn = (Button) view;
			
			String title = this.getHint();
			
			AlertDialog.Builder builder= new AlertDialog.Builder(pickerBtn.getContext());
	        builder.setTitle(title);
	        builder.setCancelable(true);
	        
	        builder.setSingleChoiceItems(options, selectedIndex , new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            	
	            	// update button text
	            	pickerBtn.setText( options[which] ); 
	            	if(optionsCode != null) {
	            		Log.d("QUICKFORM", "picker option code:" + optionsCode[which]);
	            		setValue(optionsCode[which]);
	            	} else {
	            		setValue(options[which]); // store value
	            	}
	            	selectedIndex = which;
	            	
	            	//new category selected
	                dialog.dismiss();
	                
	                // display error if fail validation
	                doValidationForUI(pickerBtn);
	            }
	        });
	        
	        
	        AlertDialog alert= builder.create();
	        alert.show();
		}
	
	}
}
