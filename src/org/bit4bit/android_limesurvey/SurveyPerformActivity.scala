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
import org.bit4bit.limesurvey.Survey
import org.bit4bit.limesurvey.LimeSurvey
import org.bit4bit.limesurvey.QuestionText
import org.bit4bit.limesurvey.QuestionListRadio
import java.util.ArrayList
import android.content.SharedPreferences
import com.ngohung.form.el.store.HPrefDataStore
import android.content.Context
import org.bit4bit.limesurvey.QuestionType
import org.bit4bit.limesurvey.QuestionDate
import org.bit4bit.limesurvey.QuestionNumerical
import org.bit4bit.limesurvey.Question
import org.bit4bit.limesurvey.Group
import scala.collection.mutable.ListBuffer
import org.bit4bit.limesurvey.Participant
import org.bit4bit.limesurvey.LimeSurveyException
import java.util.concurrent.TimeUnit
import java.util.concurrent.CountDownLatch



class SurveyPerformActivity extends HBaseFormActivity {
	private val TAG:String = "SurveyPerformActivity"

	override def onCreate(savedInstanceState:Bundle) {
	  super.onCreate(savedInstanceState)
	  this.setInstructions("* campos obligatorios :)")

	}
	
	override def onCreateOptionsMenu(menu:Menu): Boolean = {
	  //@todo menu solo de encuestas
	  getMenuInflater().inflate(R.menu.survey_perform, menu)
	  return true
	}
	
	override def onOptionsItemSelected(item:MenuItem): Boolean = {
	  item.getItemId() match {
	    case R.id.sendForm =>
	    	sendForm()
	  }
	  return super.onOptionsItemSelected(item)
	}

	def sendForm(){
  	      if(!this.checkFormData())
	    	this.displayFormErrorMsg("Error", "Hay errores en el formulario")
	      else {
	    	  	//operacion de red en otro hilo :)
	    	  	val latch = new CountDownLatch(1)
	    	  	var exception:Exception = null
	    	  	new Thread(new Runnable(){
	    	  	  override def run() {
	    	  	    try{sendResponses();}
	    	  		  catch {
	    	  		    case e:Exception => exception = e
	    	  		  }
	    	  		  latch.countDown();
	    	  	  }
	    	  	  	
	    	  	}).start()
	    	  	latch.await()
	    	  	if(exception != null)
	    	  	  throw exception
	        	val pref:SharedPreferences = this.getApplicationContext().getSharedPreferences("SurveyPerformForm", Context.MODE_PRIVATE)
	        	pref.edit().clear().commit()        
	        	this.refreshAndValidateViews()
	        	this.clearValuesElements()
	        	//finish();
	        	//startActivity(getIntent());
	        	this.displayFormErrorMsg("Success","Ok, enviado")

	      }
	}

	
	private def sendResponses() {
	  
	  var pt_firstname = "";
	  var pt_lastname = "";
	  var pt_email = "";
	  
	  var groups =  ListBuffer[Group]()
	
	  for(position <- 0 until formAdapter.getCount()) {
	    val el:HElement = formAdapter.getItem(position).asInstanceOf[HElement]
	    el.getKey() match {
	      case "participant_firstname" =>
	        pt_firstname = el.getValue()
	      case "participant_lastname" =>
	        pt_lastname = el.getValue()
	      case "participant_email" =>
	        pt_email = el.getValue()
	      case _ => 
	        val qt:Question = el.getData().asInstanceOf[Question];
	        val group:Group = qt.getGroup();
	        if(!groups.contains(group))
	        	groups += group;
	        qt.setResponse(el.getValue())
	    }
	  }
	

	  for(group <- groups) {
	    Log.d(TAG, "GroupSend:" + group.getName())
	    group.send()
	    Log.d(TAG, "GroupResponses:" + group.getResponses())
	  }
	  var survey = groups(0).getSurvey()
	  val pt = survey.addParticipant(new Participant(pt_firstname, pt_lastname, pt_email))
	  Log.d(TAG, "CreatedParticipant:Token:" + pt.getToken())
	  try {
	    survey.send(pt.getToken())
	    survey.clear_responses()  
	  } catch {
	    case e: Exception =>
	      Log.e(TAG,e.getMessage())
	    
	  }
	
	}
	


	
   def createRootElement(): HRootElement = {
	new HRootElement("Survey Form", getIntent().getExtras().get("sections_form").asInstanceOf[java.util.ArrayList[HSection]])	
	}

	
}