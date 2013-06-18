package org.bit4bit.android_limesurvey

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.view.View
import android.content.Intent
import android.widget.EditText
import android.util.Log
import android.widget.TextView
import org.bit4bit.limesurvey.Survey
import org.bit4bit.limesurvey.LimeSurvey
import android.text.Html
import org.bit4bit.android_limesurvey.controller.main.Controller

class RetreiveSurveyStatusTask extends org.unknown.util.AsyncTask[Activity,Unit,Activity] {
  private var TAG:String = "RetreiveSurveyStatusTask"
    
  private var exception:Exception = null
  private var textStatus:String = ":)"
  override protected def onProgressUpdateImpl(progress:Unit*): Unit = {
    
  }
  
  override protected def doInBackgroundImpl(params:Activity*): Activity = {
    val activity:Activity = params(0)
    exception = null
    textStatus = ""
    try {
    	val surveyId = activity.getIntent().getExtras().getInt(controller.main.Controller.SURVEY_SELECTED);
	  	val survey = Survey.fromLimeSurveyId(LimeSurvey.getInstance(), surveyId);
	 	textStatus = "Completed responses: " + survey.getSummaryCompleteResponses()	
    }catch {
      case e:Exception => exception = e
    }
    return activity
  }
  
  override protected def onPostExecute(activity:Activity) {
    if(exception != null){
    	Log.d(TAG, "Exception:"+ exception.getMessage());
    	org.unknown.util.Dialog.errorDialog(activity,exception.getMessage());
    }else {
      Log.d(TAG, "TEXTSTATUS:" + textStatus)
      activity.findViewById(R.id.survey_status_text).asInstanceOf[TextView].setText(textStatus)
    
    }
  }
}


class SurveyStatusActivity extends Activity {
    var paused:Boolean = false
	override def onCreate(savedInstanceState:Bundle) {
	  super.onCreate(savedInstanceState)	  
	 
	  //textView.setText(Html.fromHtml(survey.getStatisticsHTML()))
	  setContentView(R.layout.activity_survey_status)
	  //new RetreiveSurveyStatusTask().execute(this)
	}
	
	override def onResume() {
	  //@todo tener presente con el offline
	  new RetreiveSurveyStatusTask().execute(this)
	  super.onResume()
	}
}