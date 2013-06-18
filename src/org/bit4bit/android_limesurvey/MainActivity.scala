package org.bit4bit.android_limesurvey

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.view.View
import android.content.Intent
import android.widget._
import android.util.Log
import org.bit4bit.limesurvey._
import android.view.MenuItem
import android.preference.PreferenceManager
import android.content.SharedPreferences
import org.unknown.util.Installation
import net.sqlcipher.database.SQLiteDatabase
import android.view.View.OnClickListener
import java.io.File
import android.app.AlertDialog
import android.content.DialogInterface
import android.app.ListActivity
import android.database.Cursor
import android.content.Context
import org.bit4bit.android_limesurvey
import org.bit4bit.android_limesurvey.controller.main.Controller
import android.os.AsyncTask
import org.bit4bit.android_limesurvey.controller.main.RetreiveSurveyTask



class MainActivity extends Activity with OnClickListener {
	val TAG:String  = "MainActivity"
	val controller = new android_limesurvey.controller.main.Controller(this)
	
    override def onCreate(savedInstanceState:Bundle) : Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)    
    Log.v(TAG, "PRUEBA DE LIME SURVEY")
    Log.v(TAG, "UUID:DEVICE:" + Installation.id(this))
    bootstrap();
    
  }
	
   def bootstrap() {
    val bGetSurvey = findViewById(R.id.button_getSurvey).asInstanceOf[Button]
    bGetSurvey.setOnClickListener(this)   

  }
    def onClick(view:View) {
    //@uithread exception ya que es llamado en mainThread
    //vease http://android-developers.blogspot.com/2009/05/painless-threading.html
    view.getId() match {
      case R.id.button_getSurvey =>
      	val task = new RetreiveSurveyTask()
      	task.execute(new Controller(this))
    }
  }
   
	override def onResume() {
	  super.onResume()
	  //val settings:SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
	}
	
  override def onCreateOptionsMenu(menu:Menu) : Boolean = {
    getMenuInflater().inflate(R.menu.main, menu)
    return true
  }
  
  override def onOptionsItemSelected(item:MenuItem): Boolean = {
    Log.d(TAG, "MenuOptionSelected:" + item.getItemId().toString())
    return item.getItemId() match {
      case R.id.action_configuration =>
      	val intent = new Intent(this, classOf[ConfigurationActivity])
      	Log.d(TAG, "configuration activity")
      	startActivity(intent)
        true
      case _ => 
        super.onOptionsItemSelected(item)
    }
  }
}