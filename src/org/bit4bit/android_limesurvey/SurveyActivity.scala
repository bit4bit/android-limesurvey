/**
 * Esta actividad se inicia por actividad
 * el primer pantallaso es el summario de la encuesta
 * en el menu:
 *  - realizar encuesta
 *  - Enviar encuesta
 */
package org.bit4bit.android_limesurvey


import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.view.View
import android.content.Intent
import android.widget.EditText
import android.util.Log
import com.ngohung.form.HBaseFormActivity
import com.ngohung.form.el._
import com.ngohung.form.el.validator._
import android.view.MenuItem

class SurveyActivity extends HBaseFormActivity {
	
	override def onCreate(savedInstanceState:Bundle) {
	  super.onCreate(savedInstanceState)
	  this.setInstructions("* fields mandatory :)")
	}
	
	override def onCreateOptionsMenu(menu:Menu): Boolean = {
	  //@todo menu solo de encuestas
	  getMenuInflater().inflate(R.menu.main, menu)
	  return true
	}
	

	override def createRootElement(): HRootElement = {
	  var sections = new java.util.ArrayList[HSection]()
	  var personalInfoSection = new HSection("Informacion Personal")
	  personalInfoSection.addEl(new HTextEntryElement("firstname", "Name", "Enter your name", true))
	  val rootEl = new HRootElement("Survey Form", sections)
	  return rootEl
	}
}