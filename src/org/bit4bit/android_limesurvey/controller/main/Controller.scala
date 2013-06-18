package org.bit4bit.android_limesurvey.controller.main


import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.view.View
import android.content.Intent
import android.widget._
import android.util.Log
import org.bit4bit.limesurvey._
import android.view.MenuItem
import org.bit4bit.android_limesurvey.ConfigurationActivity
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
import org.bit4bit.android_limesurvey.R
import java.util.ArrayList
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import android.view.ViewGroup
import org.bit4bit.android_limesurvey.SurveyManagerActivity
import android.os.AsyncTask

object Controller {
    val SURVEY_SELECTED = "SURVEY_SELECTED"
}




class Controller(activity:Activity){
  private val TAG = "CONTROLLER:MainActivity:"
  private var limeSurvey:LimeSurvey = null
  private var adapterListSurvey:ArrayAdapter[Survey] = null
  private var surveys:ArrayList[Survey] = new ArrayList[Survey]()

  def getActivity() = activity
  
  /**
   * Carga encuestas del servidor remoto
   */
  def loadSurveys() {
    Log.d(TAG, "loadSurveys")
    val settings:SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
    Log.d(TAG, "serveyUrl:" + settings.getString("serverurl",""))
    Log.d(TAG, "username:" + settings.getString("username",""))
    Log.d(TAG, "password:" + settings.getString("password",""))
        
  
       var serverurl:String = settings.getString("serverurl","")
       if(serverurl == "")
         throw new RuntimeException("Debe indicar servidor encuestas")
    	limeSurvey = LimeSurvey.getInstance(serverurl,settings.getString("username", ""), settings.getString("password",""))	
        //@todo mostrar en subItem la descripcion de la campana
       surveys = limeSurvey.listSurveys()
  }
  
  def createAdapterListSurvey():ArrayAdapter[Survey] = {
    return new ArrayAdapter[Survey](activity, android.R.layout.simple_list_item_1, surveys)
  }
  
  def errorDialog(msg:String){
    org.unknown.util.Dialog.errorDialog(activity, msg)
  }
  
  

}