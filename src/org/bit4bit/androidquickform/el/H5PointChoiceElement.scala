package org.bit4bit.androidquickform.el

import java.util.Map;

import com.ngohung.form.constant.HConstants;
import com.ngohung.form.el.store.HDataStore;
import com.ngohung.form.el.validator.ValidationStatus;
import com.ngohung.form.util.HStringUtil;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.View;


class H5PointChoiceElement(key:String, label:String, hint:String, required:Boolean) extends HPickerElement2(key, label, hint, required, Array( "No Responde", "1","2","3","4","5"), Array("","1","2","3","4","5")) {
}