package org.bit4bit.android_limesurvey

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.view.View
import android.content.Intent
import android.widget.EditText
import android.util.Log
import android.content.SharedPreferences
import android.preference.PreferenceActivity


class ConfigurationActivity extends PreferenceActivity {
	private val TAG = "ConfigurationActivity"
	  
	protected override def onCreate(savedInstanceState:Bundle) {
	  super.onCreate(savedInstanceState)
	  addPreferencesFromResource(R.xml.preferences)
	}
	
}