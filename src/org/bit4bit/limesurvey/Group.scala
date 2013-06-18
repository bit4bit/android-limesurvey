package org.bit4bit.limesurvey

import org.json._
import java.util.ArrayList
import scala.collection.mutable.Map

object Group {
  def fromJSONObject(survey:Survey, data:JSONObject): Group = {
    var group:Group = new Group(survey, Integer.parseInt(data.getJSONObject("id").get("gid").asInstanceOf[String]),
        data.get("group_name").asInstanceOf[String])
    group.setLanguage(data.getJSONObject("id").get("language").asInstanceOf[String])
    return group
  }
}

class Group(ssurvey:Survey, Id:Integer, Name:String) {
	private var id:Integer = Id;
	private var name:String = Name;
	private var survey:Survey = ssurvey;
	private var language:String = null
	private val responses = Map.empty[String,String]
	private var participantToken:String = null
	
	def getId(): Integer = { id }
	def getName(): String  = { name }
	def getLanguage(): String = {language}
	def setLanguage(lang:String) { language = lang}
	def getSurvey(): Survey = { survey }
	
	def getQuestions(): Array[Question] = {
	  var data = survey.list_questions(getId(), language)
      var questions:Array[Question] = new Array[Question](data.length())
			  
	 for(i <- 0 until data.length()) {
	   questions(i) = Question.fromJSONObject(this, data.getJSONObject(i))
	 }

	  return questions
	}
	
	def getQuestionsObject(): Array[Object] = {
	    var data = survey.list_questions(getId(), language)
      var questions:Array[Object] = new Array[Object](data.length())
			  
	 for(i <- 0 until data.length()) {
	   questions(i) = Question.fromJSONObject(this, data.getJSONObject(i))
	 }

	  return questions
	}
	
	def setParticipantToken(token:String) {
	  participantToken = token
	}
	
	def addResponse(questionId:Integer, value:String) {
	  responses(getSurvey().getId() + "X" + getId() + "X" + questionId) = value
	}
	
	def addResponse(questionId:String, value:String) {
	  responses(getSurvey().getId() + "X" + getId() + "X" + questionId) = value
	}
	
	//envia respuesta de grupo de preguntas
	def send() {
	  getSurvey().add_responses(responses)
	}
	
	def getResponses(): Map[String,String] = {
	  return responses
	}
}