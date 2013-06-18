package org.bit4bit.android_limesurvey

import android.os.Bundle
import android.app.Activity
import android.view.Menu
import android.view.View
import android.content.Intent
import android.widget.EditText
import android.util.Log
import android.widget.TextView
import android.app.TabActivity
import com.ngohung.form.el._
import org.bit4bit.limesurvey.Survey
import java.util.concurrent.CountDownLatch
import org.bit4bit.limesurvey.LimeSurvey
import org.bit4bit.limesurvey._
import android.view.MenuItem
import org.bit4bit.androidquickform.el.HYesNoElement
import org.bit4bit.androidquickform.el.H5PointChoiceElement
import org.bit4bit.androidquickform.el.HMultiPickerElement
import org.bit4bit.androidquickform.el.HPickerElement2


class RetreiveSurveyFormTask extends org.unknown.util.AsyncTask[SurveyPerformActivity,Unit,SurveyPerformActivity] {
  private val TAG:String = "RetreiveSurveyFormTask"
  private var exception:Exception = null
  private var sections:java.util.ArrayList[HSection] = new java.util.ArrayList[HSection]()
  override protected def onProgressUpdateImpl(progress:Unit*): Unit = {
    
  }
  
  //@todo FALTA OBTENER DESDE LIMESURVEY SI ES OBLIGATORIO O NO
  def createForm(activity:Activity): java.util.ArrayList[HSection] = {
     var sections:java.util.ArrayList[HSection] = new java.util.ArrayList[HSection]()
	  val surveyId = activity.getIntent().getExtras().getInt(controller.main.Controller.SURVEY_SELECTED)
	  val survey = Survey.fromLimeSurveyId(LimeSurvey.getInstance(), surveyId)
	  Log.d(TAG, "SurveyToPerform:id " + survey.getId())

	  //FORMULARIO DE PARTICIPANTE
	  val participantSection = new HSection("Encuestado/a") 
	  sections.add(participantSection)
	  participantSection.addEl(new HTextEntryElement("participant_firstname","Nombre/s","",true))
	  participantSection.addEl(new HTextEntryElement("participant_lastname","Apellido/s","",false))
	  participantSection.addEl(new HTextEntryElement("participant_email","Correo-E","",false))
	  //@todo falta saber como leer los atributos adicionles de los participantes

	  //var personalInfoSection = new HSection("Informacion Personal")
	  for(group <- survey.getGroupsArray()) {
	    Log.d(TAG,"GroupToPerform:name" + group.getName())
	    //se crea seccion por grupo de preguntas
	    var infoSection = new HSection(group.getName())
	    //se crean preguntas
	    for(question <- group.getQuestions()) {
	      Log.d(TAG, "QuestionOfGroup:" + question.getTypeCode() )
	      var  qElement:HElement = null
	      question match {
	        case qt:Question5PointChoice =>
	          qElement = new H5PointChoiceElement(qt.getId().toString(), qt.getQuestion(), "", qt.getMandatory())
	        case qt:QuestionYesNo =>
	          qElement = new HYesNoElement(qt.getId().toString(), qt.getQuestion(), "", qt.getMandatory())
	        case qt:QuestionNumerical =>
	          qElement =  new HNumericElement(qt.getId().toString(), qt.getQuestion(), "", qt.getMandatory())
	        case qt:QuestionDate =>
	          qElement = new HDatePickerElement(qt.getId().toString(), qt.getQuestion(), "", qt.getMandatory())
	        case qt:QuestionText =>
	          qt.getTypeCode() match {
	            case QuestionType.SHORT_FREE_TEXT =>
	              	     qElement = (new HTextEntryElement(qt.getId().toString(),qt.getQuestion(),"",qt.getMandatory()))
	            case QuestionType.LONG_FREE_TEXT =>
	              	     qElement = (new HTextAreaEntryElement(qt.getId().toString(),qt.getQuestion(),"",qt.getMandatory()))
	            case QuestionType.HUGE_FREE_TEXT =>
	              	     qElement = (new HTextAreaEntryElement(qt.getId().toString(),qt.getQuestion(),"",qt.getMandatory()))

	          }
	        case ql:QuestionListRadio =>
	          var options = ql.getOptions()
	          var items = new Array[String](options.length)
	          var codes = new Array[String](options.length)
	          for(i <- 0 until options.length) {
	            items(i) = options(i).getValue()
	            codes(i) = options(i).getCode()
	          }
	      
	          qElement = (new HPickerElement2(ql.getId().toString(), ql.getQuestion(),"",ql.getMandatory(), items, codes))
	        case ql:QuestionMultipleChoice =>
	          var options = ql.getOptions()
	          var items = new Array[String](options.length)
	          var codes = new Array[String](options.length)
	          for(i <- 0 until options.length) {
	            items(i) = options(i).getValue()
	            codes(i) = options(i).getCode()
	          }
	          //@todo falta opciones
	          qElement = new HMultiPickerElement(ql.getId().toString(), ql.getQuestion(), "", ql.getMandatory(), items, codes)
	        case _ =>
	          ;
	      }
	      if(qElement != null) {
	        qElement.setData(question)
	        infoSection.addEl(qElement)
	      }
	    }
	    sections.add(infoSection)
	  }
     return sections
	  //personalInfoSection.addEl(new HTextEntryElement("firstname", "Name", "Enter your name", true))
  }
  
  override protected def doInBackgroundImpl(params:SurveyPerformActivity*):SurveyPerformActivity = {
    val activity = params(0)
    try {
      sections = createForm(activity);
    }catch{
      case e:Exception => exception = e
    }
    return activity
  }
  
  override protected def onPostExecute(activity:SurveyPerformActivity) {
    if(exception != null) {
      org.unknown.util.Dialog.errorDialog(activity, exception.getMessage())
    }else{
      
    }
  }
}

class SurveyManagerActivity extends TabActivity {
	val TAG = "SurveyManagerActivity"
	  var surveyId:Int = 0
	override def onCreate(savedInstanceState:Bundle) {
	  super.onCreate(savedInstanceState)	  
	  val extras:Bundle = getIntent().getExtras()
	  surveyId = extras.getInt(controller.main.Controller.SURVEY_SELECTED)
	  val latch = new CountDownLatch(1)
	  //PRECARGA LOS ELEMENTOS PARA QUICKFORM
	  //ENBASE AL LA ENCUESTA
	  var sections = new java.util.ArrayList[HSection]()
	  new Thread(new Runnable(){
	    override  def run(){
	      val task = new RetreiveSurveyFormTask()
	      sections = task.createForm(SurveyManagerActivity.this)
	      latch.countDown()
	    }
	  }).start()
	  latch.await()
	  
	  val tabHost = getTabHost()
	  
	  //http://developer.android.com/guide/topics/ui/layout/tabs.html
	  
	  //estado de encuestado sumario/resumen
	  var intent = new Intent(this, classOf[SurveyStatusActivity])
	  intent.putExtra(controller.main.Controller.SURVEY_SELECTED, surveyId)
	  var spec  = tabHost.newTabSpec("status").setIndicator("status").setContent(intent)
	  tabHost.addTab(spec)
	  //realiza encuesta
	  //@todo NO CORRE
	  intent = new Intent(this, classOf[SurveyPerformActivity])
	  intent.putExtra(controller.main.Controller.SURVEY_SELECTED, surveyId)

	  intent.putExtra("sections_form", sections)
	  spec = tabHost.newTabSpec("survey").setIndicator("survey").setContent(intent)
	  tabHost.addTab(spec)
	  
	  tabHost.setCurrentTab(0)
	}
	
		
	override def onCreateOptionsMenu(menu:Menu): Boolean = {
	  //@todo menu solo de encuestas
	  //@TODO MOSTRAR ENVIAR
	  getMenuInflater().inflate(R.menu.survey_perform, menu)
	  return true
	}
	
	override def onOptionsItemSelected(item:MenuItem): Boolean = {
	  item.getItemId() match {
	    case R.id.sendForm =>
	      try {
	        getLocalActivityManager().getActivity("survey").asInstanceOf[SurveyPerformActivity].sendForm()
	      }catch{
	        case e:Exception => org.unknown.util.Dialog.errorDialog(this, e.getMessage())
	      }

	  }
	  return super.onOptionsItemSelected(item)
	}
}