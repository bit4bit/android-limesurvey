package org.bit4bit.limesurvey
import org.json._
import android.util.Log
import scala.collection.immutable.HashMap


object Question {
  def fromJSONObject(gGroup:Group, data:JSONObject): Question = {
    var attr = data.getJSONObject("id")
    var question_type = data.get("type").asInstanceOf[String]
    var question_name = data.get("question").asInstanceOf[String]
    var question_id = Integer.parseInt(attr.get("qid").asInstanceOf[String])
   
    val propertiesObject = gGroup.getSurvey().get_question_properties(question_id, Array("mandatory"))
    val question_mandatory =  if (propertiesObject.asInstanceOf[JSONObject].getString("mandatory") == "N") false else true
    Log.d("Question", "question_mandatory:" + question_mandatory)
    
    var question = question_type match {
      case QuestionType.MULTIPLE_CHOICE => new QuestionMultipleChoice(gGroup, question_id, question_name, question_mandatory)
      case QuestionType.NUMERICAL_INPUT => new QuestionNumerical(gGroup, question_id, question_name, question_mandatory)
      case QuestionType.DATE => new QuestionDate(gGroup, question_id, question_name, question_mandatory)
      case QuestionType.SHORT_FREE_TEXT =>  new QuestionText(gGroup, question_id, question_name, question_type, question_mandatory)
      case QuestionType.HUGE_FREE_TEXT => new QuestionText(gGroup, question_id, question_name, question_type, question_mandatory)
      case QuestionType.LONG_FREE_TEXT => new QuestionText(gGroup, question_id, question_name, question_type, question_mandatory)
      case QuestionType.LIST_RADIO => new QuestionListRadio(gGroup, question_id, question_name, question_mandatory)
      case QuestionType.YESNO => new QuestionYesNo(gGroup, question_id, question_name, question_mandatory)
      case QuestionType.FIVE_POINTCHOICE => new Question5PointChoice(gGroup, question_id, question_name, question_mandatory)
      case _ => new QuestionUnknown(gGroup, question_type, question_id, question_name, question_mandatory)
    }
    return question
  }
}

abstract class Question {
	 var group:Group
	 var questionId:Integer
	 var question:String
	var response:String = ""
	 var mandatory:Boolean //@todo pasar esto y otros parametros como objecto de propiedades
	 
	def getId(): Integer = { questionId}
	def getQuestion(): String = {question}
	def getGroup(): Group = { group }
	def getTypeCode(): String
	def getResponse(): String = response
	def getMandatory(): Boolean = mandatory
	
	/**
	 * Se registra respuesta ya para enviar
	 */
	def setResponse(data:String) {
	  response = data
	  group.addResponse(getId(), response)
	  }
	def addParticipantResponse(token:String, data:String): Integer
	
}

class QuestionUnknown(gGroup:Group, question_type:String, question_id:Integer,  qquestion:String, required:Boolean = false) extends Question {
  	val TAG  = "QuestionUnknow"
  var group = gGroup
	var questionId = question_id
	var question = qquestion
	var questionType = question_type
	var mandatory = required
	def getTypeCode(): String = { 
  	   Log.e(TAG, "QuestionUnknown::TypeCode:" + questionType)
  	  return questionType 
  	  }
  	

  	
  	def addParticipantResponse(token:String, data:String):Integer = {
  	  0
  	}
}




class QuestionText(gGroup:Group, question_id:Integer,qquestion:String, _typeCode:String, required:Boolean = false) extends Question {
	var group = gGroup
	var questionId = question_id
	var question = qquestion
	var typeCode:String = _typeCode
	var mandatory = required
	
	def getTypeCode(): String = { typeCode}
	def setTypeCode(t:String) {
	  typeCode = t
	}
	

def addParticipantResponse(token:String, data:String): Integer = {
	  group.getSurvey().add_response(token, this, data)
	}	
}


class QuestionOption(question:Question, code:String, value:String) {
  def getCode(): String = {code}
  def getValue(): String = {value}
  override def toString(): String = { "QuestionOption(" + code +"," + value + ")"}
}


class QuestionListRadio(gGroup:Group, question_id:Integer, qquestion:String, required:Boolean = false) extends Question {
  	var group = gGroup
	var questionId = question_id
	var question = qquestion
	var mandatory = required
	
	def getTypeCode(): String = { QuestionType.LIST_RADIO }
  	
  
  	
	def addResponseOption(option:QuestionOption) {
	  response = option.getCode()
	}
  	
  	def addParticipantResponse(token:String, data:String): Integer = {
  	  0
  	}
  	
  	/**
  	 *@return HashMap[Codigo, Contenido]
  	 */
  	def getOptions(): Array[QuestionOption]  = {
  	 val answeroptions = getGroup().getSurvey().getLimeAgent().get_question_property(getId(), "answeroptions", getGroup().getLanguage()) 
  	 answeroptions match {
  	    case o:JSONObject =>
  	      if(o.has("status")) {
  	        throw new LimeSurveyException(o.get("status").toString())
  	      }else{
  	        val oo = o.getJSONObject("answeroptions")
  	        val it = oo.keys()
  	        val options = new Array[QuestionOption](oo.length())
  	        var count = new Integer(0)
  	       while(it.hasNext()) {
  	         var name = it.next().asInstanceOf[String]
  	         options(count) = new QuestionOption(this, name, oo.getJSONObject(name).get("answer").toString())
  	         count = count + 1 //@todo que codigo mas pelle..pero aun
  	       }
  	        options
  	      }
  	    case _ => new Array[QuestionOption](0)
  	  }
 
  	}
}

class QuestionYesNo(gGroup:Group, question_id:Integer, qquestion:String, required:Boolean = false) extends Question {
  var group = gGroup
  var questionId = question_id
  var question = qquestion
  var mandatory = required
  
  def getTypeCode(): String = QuestionType.YESNO
  
  def addResponseOption(option:QuestionOption) {
    response = option.getCode()
  }
  
  def addParticipantResponse(token:String, data:String): Integer = {
  	  0
  	}
}

class Question5PointChoice(gGroup:Group, question_id:Integer, qquestion:String, required:Boolean = false) extends Question {
  var group = gGroup
  var questionId = question_id
  var question = qquestion
  var mandatory = required
  
  def getTypeCode(): String = QuestionType.FIVE_POINTCHOICE
  
  def addResponseOption(option:QuestionOption) {
    response = option.getCode()
  }
  
  def addParticipantResponse(token:String, data:String): Integer = {
  	  0
  	}
}

class QuestionMultipleChoice(gGroup:Group, question_id:Integer,  qquestion:String, required:Boolean = false) extends Question {
  	var group = gGroup
	var questionId = question_id
	var question = qquestion
	var mandatory = required
	def getTypeCode(): String = { "M" }
  	
	override def setResponse(data:String) {
	  response = data
	  for(code <- data.split(",")) {
	    group.addResponse(getId() + code, "Y")
	  }
	}
  	
  	def addResponseOption(option:QuestionOption) {
	  response = option.getCode()
	}
  	
  	def addParticipantResponse(token:String, data:String): Integer = {
  	  0
  	}
  	
  	/**
  	 *@return HashMap[Codigo, Contenido]
  	 */
  	def getOptions(): Array[QuestionOption]  = {
  	 val answeroptions = getGroup().getSurvey().getLimeAgent().get_question_property(getId(), "subquestions", getGroup().getLanguage()) 
  	 answeroptions match {
  	    case o:JSONObject =>
  	      if(o.has("status")) {
  	        throw new LimeSurveyException(o.get("status").toString())
  	      }else{
  	        val oo = o.getJSONObject("subquestions")
  	        val it = oo.keys()
  	        val options = new Array[QuestionOption](oo.length())
  	        var count = new Integer(0)
  	       while(it.hasNext()) {
  	         var key = it.next().asInstanceOf[String]
  	         var name:String = oo.getJSONObject(key).get("title").toString() //limesurvey usa este para columnas
  	         options(count) = new QuestionOption(this, name, oo.getJSONObject(key).get("question").toString())
  	         count = count + 1 //@todo que codigo mas pelle..pero aun
  	       }
  	        options
  	      }
  	    case _ => new Array[QuestionOption](0)
  	  }
 
  	}
}

class QuestionDate(gGroup:Group, question_id:Integer, qquestion:String, required:Boolean = false) extends Question {
   	var group = gGroup
	var questionId = question_id
	var question = qquestion
	var mandatory = required
	def getTypeCode(): String = { "D" }
  	
   	def addParticipantResponse(token:String, data:String): Integer = {
	  group.getSurvey().add_response(token, this, data)
	}
}

class QuestionNumerical(gGroup:Group, question_id:Integer, qquestion:String, required:Boolean = false) extends Question {
   	var group = gGroup
	var questionId = question_id
	var question = qquestion
	var mandatory = required
	
	def getTypeCode(): String = { "N" }
  	
   	def addParticipantResponse(token:String, data:String): Integer = {
	  group.getSurvey().add_response(token, this, data)
	}
}

