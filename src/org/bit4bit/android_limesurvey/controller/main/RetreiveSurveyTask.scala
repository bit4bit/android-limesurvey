package org.bit4bit.android_limesurvey.controller.main

import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.AdapterView._
import android.widget.AdapterView
import android.content.Intent
import android.app.Activity
import org.bit4bit.limesurvey.LimeSurveyException
import org.bit4bit.android_limesurvey.R
import org.bit4bit.android_limesurvey.SurveyManagerActivity
import org.bit4bit.limesurvey._




class RetreiveSurveyTask extends org.unknown.util.AsyncTask[Controller,Unit, Controller] {
  val TAG:String = "RetreiveSurveyTask"
    
  private var exception:Exception = null
  
  override  protected def onPreExecute() {  
  }
  
 	override protected  def onProgressUpdateImpl(progress:Unit*): Unit = {
 	}
	
 	override protected def doInBackgroundImpl(params:Controller*): Controller = {
 	  val controller:Controller = params(0)
 	  try {
 	  controller.loadSurveys()
 	  } catch {
 	    case e:Exception => 
 	      exception = e
 	  }
 	  return controller
 	}
 	
   override protected def onPostExecute(controller:Controller) {
     if(exception != null) {
       Log.d(TAG, "Exception:"+ exception.getMessage())
       controller.errorDialog(exception.getMessage())
     }else {
       //@todo RuntimeExcpetion: NetmworkOnMainThread
         val listViewSurvey = controller.getActivity().findViewById(R.id.listView_survey).asInstanceOf[ListView]
            listViewSurvey.setAdapter(controller.createAdapterListSurvey())
       listViewSurvey.setOnItemClickListener( new OnItemClickListener() {
           override def onItemClick(parent:AdapterView[_], view:View, position:Int, id:Long) {
        	 val survey = listViewSurvey.getItemAtPosition(position).asInstanceOf[Survey]

            if(survey.active()){
               Log.d(TAG, listViewSurvey.getItemAtPosition(position).asInstanceOf[Survey].getId().toString())
               val intent = new Intent(controller.getActivity().getApplicationContext(), classOf[SurveyManagerActivity])
               intent.putExtra(Controller.SURVEY_SELECTED, listViewSurvey.getItemAtPosition(position).asInstanceOf[Survey].getId())
               controller.getActivity().startActivity(intent)
            }else{
              controller.errorDialog(controller.getActivity().getString(R.string.survey_not_active))
            }
          }
        })
     }
  }
}