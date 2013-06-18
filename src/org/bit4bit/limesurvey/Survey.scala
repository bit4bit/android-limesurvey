package org.bit4bit.limesurvey

import org.json._
import scala.collection.immutable.HashMap
import scala.collection.mutable.Map
import android.util.Base64


object Survey {
  def fromLimeSurveyId(limeSurvey:LimeSurvey, surveyId:Integer): Survey = {
	val surveyProperties = limeSurvey.get_survey_properties(surveyId, Array("sid","active", "language", "expires", "startdate"))
	val languageProperties = limeSurvey.get_language_properties(surveyId, surveyProperties.get("language").toString(),
			Array("surveyls_title"))
	val survey = new JSONObject()
	val iSurveyProperties = surveyProperties.keys()
	while(iSurveyProperties.hasNext()) {
	  val key:String = iSurveyProperties.next().asInstanceOf[String]
	  survey.put(key, surveyProperties.get(key))
	}
	val iLanguageProperties = languageProperties.keys()
	while(iLanguageProperties.hasNext()) {
	  val key:String = iLanguageProperties.next().asInstanceOf[String]
	  survey.put(key, languageProperties.get(key))
	}
	return new Survey(limeSurvey, survey)
  }
}

class Survey(limeSurvey:LimeSurvey, surveyO:JSONObject) {
	private var limeAgent:LimeSurvey = limeSurvey
	private var surveyData:JSONObject = surveyO.asInstanceOf[JSONObject]
	private var responses = Map.empty[String, String]
	//??si va aca
	def getLimeAgent(): LimeSurvey = { limeAgent }
	
	def getTitle(): String = {
	  return surveyData.get("surveyls_title").asInstanceOf[String]
	}
	
	def id(): Int = {
	  return Integer.parseInt(surveyData.get("sid").asInstanceOf[String])
	}
	
	def getId(): Integer = { 
	  return Integer.parseInt(surveyData.get("sid").asInstanceOf[String])
	}
	
	def active(): Boolean = {
	  return if(surveyData.get("active") == "Y") true else false
	}
	
	override def toString(): String = {
	  return getTitle()
	}
	
	//@todo como manejar DATE 
	def startDate(): String = {
	  //return surveyData.get("startdate").asInstanceOf[String]
	  return ""
	}
	
	//@todo como manejar DATE
	def expireDate(): String = {
	  //return surveyData.get("expires").asInstanceOf[String]
	  return ""
	}
	
	def getProperty(property:String): String = {
	  try
	  {
	     var properties = limeAgent.get_survey_properties(id(), Array(property))
		  return properties.get(property).asInstanceOf[String]
	  }catch{
	    case e:LimeSurveyException => return ""
	  }
	}
	
	def getLanguageProperty(property:String, language:String = getProperty("language") ): String = {
	  try
	  {
	    var properties = limeAgent.get_language_properties(id(), language, Array(property))
	    return properties.get(property).asInstanceOf[String]
	  }catch{
	    case e:LimeSurveyException => return ""
	  }
	}
	
	def getSummaryCompleteResponses(): Integer = {
	  return limeAgent.get_summary(id(), "completed_responses")
	}
	
	def getSummaryFullResponses(): Int = {
	  return limeAgent.get_summary(id(), "full_responses")
	}
	
	def getStatisticsHTML(): String = {
	  return new String(Base64.decode(limeAgent.export_statistics(getId(),"html"), Base64.DEFAULT),Base64.DEFAULT)
	}
	
	
	def getGroups(): java.util.HashMap[Integer, Group] = {
	  var data = limeAgent.list_groups(id())	  
	  var groups = new java.util.HashMap[Integer, Group]
	  
	  data match {
	    case o:JSONArray =>
	      for(i <- 0 to o.length() - 1) {
	        var group = Group.fromJSONObject(this, o.getJSONObject(i))
	        groups.put(group.getId(), group)	   
	      }
	  }
	  return groups
	}
	
	def getGroupsArray(): Array[Group] = {
	    var data = limeAgent.list_groups(id())	  
	  var groups = new Array[Group](data.length())
	  
	  data match {
	    case o:JSONArray =>
	     for(i <- 0 until data.length()) {
	       groups(i) = Group.fromJSONObject(this, o.getJSONObject(i))
	     }
	    }
	  return groups
	}
	
	def getParticipants(): Array[Participant] = {
	  var data:JSONArray = limeAgent.list_participants(id())
	  var participants:Array[Participant] = new Array[Participant](data.length())
	  for(i <- 0 until data.length()) {
	    participants(i) = Participant.fromJSONObject(data.getJSONObject(i))
	  }
	  return participants
	}
	
	def addParticipant(participant:Participant): Participant = {
	  var data = scala.collection.mutable.Map.empty[String,String]
	  data("firstname") = participant.getFirstName()
	  data("lastname") = participant.getLastName()
	  data("email") = participant.getEmail()
	  data("language") = getProperty("language")
	  
	  return  Participant.fromJSONObject(limeAgent.add_participant(id(), data))
	}
	
	//delega
	def list_questions(group_id:Integer, language:String = getProperty("language")): JSONArray = {
	  return limeAgent.list_questions(id(), group_id, language)
	}
	
	def get_question_properties(questionId:Integer, settings:Array[String], language:String = getProperty("language")): Object = {
	  return limeAgent.get_question_properties(questionId, settings, language )
	}
	
	def clear_responses() {
	  responses = Map.empty[String,String]
	}
	
	//@return enviar y retorna id respuestas
	def send(token:String = null, _responses:Map[String,String] = null): Integer = {
	  responses("token") = token

	  limeAgent.add_response(id(), responses)
	}
	
	def add_responses(vdata:Map[String,String]) {
	  for((name, value) <- vdata)
	    responses(name) = value
	}
	
	def addResponse(vdata:Array[Question]): Integer = {
	   val data = scala.collection.mutable.Map.empty[String,String]
	   for(question <- vdata)
	     data(limeAgent.field_response(question)) = question.getResponse()
	  return limeAgent.add_response(id(), data)
	}
	
	def add_response(name:String, value:String) {
	  responses(name) = value
	  //return limeAgent.add_response(id(), data)
	}
	
	def add_response(question:Question, value:String) {
	  add_response(getLimeAgent().field_response(question), value)
	}
	
	def add_response(token:String, question:Question, value:String): Integer = {
	  val data = scala.collection.mutable.Map.empty[String,String]
	  data("token") = token
	  data(getLimeAgent().field_response(question)) = value
	  return limeAgent.add_response(id(), data)
	}
	def add_response(participant:Participant, question:Question, value:String):Integer = {
		return add_response(participant.getToken(), question, value)
	}

}