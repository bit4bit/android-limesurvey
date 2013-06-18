package org.bit4bit.limesurvey

import scala.collection.mutable.{Map}
import java.lang.String
import org.alexd.jsonrpc.JSONRPCClient
import org.alexd.jsonrpc.JSONRPCParams
import org.json.{JSONObject, JSONArray}
import scala.collection
import scala.collection.immutable.HashMap
import java.util.ArrayList

class LimeSurveyException(msg:String) extends RuntimeException(msg)

object LimeSurvey {
  var limeSurvey:LimeSurvey = null
  
  
  def getInstance(url:String="",user:String="",pass:String=""): LimeSurvey = {
    if(limeSurvey == null) {
     limeSurvey = new LimeSurvey(url)
     try {
          limeSurvey.login(user, pass);	
     }catch {
       case e:Exception =>
    	 limeSurvey = null
         throw e
     }
    }
    
    return limeSurvey
  }
  
}
/**
 * Agente Limesurvey
 * JSONRPCClient.call retorna object..que hay que converitr a JSONObject,JSONArray etc..
 */
class LimeSurvey(service_url:String) {
	private var json_client:JSONRPCClient = JSONRPCClient.create(service_url, JSONRPCParams.Versions.VERSION_2)
	private var session_key:String = "";
	private var username:String = "";
	private var password:String = ""
	  
	  
	def login(Username:String, Password:String): Boolean = {
	  username = Username; password = Password
	  var Rjson = json_client.call("get_session_key", username, password)
	  json_client.setDebug(true)
	  session_key = ""
	  Rjson match {
	    case s:String =>
	      session_key = s
	      return true
	    case o:JSONObject =>
	      throw new LimeSurveyException(o.getString("status"))

	  }
	  return false
	}
	
	
	def logout(): Boolean = {
	  json_client.call("release_session_key", session_key)
	  return true
	}
	
	//@todo no funciona LimeSurvey..get_summary retorna que metodo o parametros incorrectos
	def get_summary(surveyId:Integer, statName:String): Int = {
	  var Rjson = json_client.call("get_summary", session_key, surveyId, statName)
	  return Rjson match {
	    case s:String => Integer.parseInt(s)
	    case m:JSONArray => -1
	      //throw new LimeSurveyException(m.toString())
	  }
	}
	
	def export_statistics(surveyId:Integer, docType:String = "html"): String = {
	  var Rjson = json_client.call("export_statistics", session_key, surveyId, docType)
	  return Rjson match {
	    case s:String => s
	    case m:JSONArray => 
	      throw new LimeSurveyException(m.toString())
	  }
	}
	
	def get_survey_properties(surveyId:Integer, properties:Array[String]): JSONObject = {
	  var JSONProperties = new JSONArray()
	  for(property <- properties) JSONProperties.put(property)
	  var Rjson = json_client.call("get_survey_properties", session_key, surveyId, JSONProperties)
	  Rjson match {
	    case ar:JSONObject =>
	      if(ar.has("status")) {
	        throw new LimeSurveyException(ar.get("status").asInstanceOf[String])
	      }else{
	        return ar
	      }
	  }
	}
	
	def get_language_properties(surveyId:Integer, language:String, properties:Array[String]): JSONObject = {
	  var JSONProperties = new JSONArray()
	  for(property <- properties) JSONProperties.put(property)
	  var Rjson = json_client.call("get_language_properties", session_key, surveyId, JSONProperties, language)
	  Rjson match {
	    case ar:JSONObject =>
	      if(ar.has("status")) {
	        throw new LimeSurveyException(ar.get("status").asInstanceOf[String])
	      }else{
	        return ar
	      }
	  }
	}
	
	def add_participant(surveyId:Integer, data:Map[String,String] ): JSONObject = {
	  val jdata = new JSONObject()
	  for((name, value) <- data) {
	    jdata.put(name, value)
	  }
	  val Rjson = json_client.call("add_participants", session_key, surveyId, new JSONArray().put(jdata))
	  return Rjson match {
	    case o:JSONObject => 
	      if(o.has("status")) throw new LimeSurveyException(o.get("status").toString())
	      o
	    case oa:JSONArray =>
	      oa.getJSONObject(0)
	  }
	}
	
	def list_participants(surveyId:Integer): JSONArray ={
	  val Rjson = json_client.call("list_participants", session_key, surveyId)
	  return Rjson match {
	    case o:JSONObject =>
	      if(o.has("status")) {
	        //@todo como manejar cuando no hay participantes
	        if(o.get("status").toString() == "No Tokens found") return new JSONArray()
	        throw new LimeSurveyException(o.get("status").toString())
	      }
	      else return o.asInstanceOf[JSONArray]
	    case oa:JSONArray =>
	      return oa
	  }
	}
	
	def list_groups(surveyId:Integer): JSONArray = {
	  var Rjson = json_client.call("list_groups", session_key, surveyId)
	  return Rjson match{
	    case o:JSONObject =>
	      if(o.has("status")) throw new LimeSurveyException(o.get("status").asInstanceOf[String])
	      else return o.asInstanceOf[JSONArray]
	    case oa:JSONArray =>
	      return oa
	  }
	}
	
	def list_questions(surveyId:Integer, groupId:Integer, language:String): JSONArray = {
	   var Rjson = json_client.call("list_questions", session_key, surveyId, groupId, language)
	  return Rjson match{
	    case o:JSONObject =>
	      if(o.has("status")) throw new LimeSurveyException(o.get("status").asInstanceOf[String])
	      else return o.asInstanceOf[JSONArray]
	    case oa:JSONArray =>
	      return oa
	  }
	}
	
	def get_question_property(questionId:Integer, property:String, language:String): Object = {
	  val properties =  new JSONArray()
	  properties.put(property)
	   return json_client.call("get_question_properties", session_key, questionId, properties, language)
	}
	
	def get_question_properties(questionId:Integer, properties:Array[String], language:String): Object = {
	  val JSONProperties = new JSONArray()
	  for(property <- properties) JSONProperties.put(property)
	  return json_client.call("get_question_properties", session_key, questionId, JSONProperties, language)
	}
	
	def add_response(surveyId:Integer, data:Map[String,String]):Integer = {
	  var response = new JSONObject()
	  for((name, value) <- data) {
	    response.put(name, value)
	  }
	  var Rjson = json_client.call("add_response", session_key, surveyId, response)
	  return Rjson match{
	    case o:String =>
	      return Integer.parseInt(o)
	    case o:JSONObject =>
	      if(o.has("status")) throw new LimeSurveyException(o.get("status").asInstanceOf[String])
	      else return Integer.parseInt(o.asInstanceOf[String])
	  }
	}
	//@todo debe cambiarse a solo json
	def list_surveys(): java.util.HashMap[Integer,Survey] = {
	  var Rjson = json_client.call("list_surveys", session_key, username)
	  Rjson match {
	    case o:JSONObject =>
	      throw new LimeSurveyException(o.getString("status"))
	    case rarray:JSONArray =>
	  var rv = new java.util.HashMap[Integer,Survey];
	  
	  for(i <- 0 to rarray.length() - 1) {
		  val survey = new Survey(this, rarray.get(i).asInstanceOf[JSONObject])	 
		  rv.put(survey.getId(),survey)
	   }
	  	  return rv

	  }
	
	}
	
	//
	def listSurveys(): ArrayList[Survey] = {
			var Rjson = json_client.call("list_surveys", session_key, username);
			Rjson match {
			  case o:JSONObject =>
			    throw new LimeSurveyException(o.getString("status"))
			  case rarray:JSONArray =>
			    	var rv = new ArrayList[Survey]();
	  
			for(i <- 0 to rarray.length() - 1) {
				rv.add(new Survey(this, rarray.get(i).asInstanceOf[JSONObject]))	  
			}
			return rv
			}
		
	}


	def field_response(surveyId:Integer, groupId:Integer, questionId:Integer): String = {
	    return surveyId + "X" +  groupId + "X" + questionId
	}
	
	//@todo si deberia ir?
	def field_response(question:Question): String = {
		return question.getGroup().getSurvey().getId() + "X" + question.getGroup().getId() + "X" + question.getId()
	}
}