package org.bit4bit.limesurvey

import org.json.JSONObject
import java.util.UUID

object Participant {
  def fromJSONObject(data:JSONObject): Participant ={
    var p:Participant = null
    if(data.has("participant_info")) {
      val info = data.getJSONObject("participant_info")
      p = new Participant(
         info.get("firstname").toString(),  
          info.get("lastname").toString(),
          info.get("email").toString(), "")
          p.setId(Integer.parseInt(data.get("tid").toString()))
          p.setToken(data.get("token").toString()) 
       
    }else {
     p = new Participant(
            data.get("firstname").toString(),		
    		data.get("lastname").toString(),
    		data.get("email").toString(),
    		"")
    		p.setId(Integer.parseInt(data.get("tid").toString()))
    		p.setToken(data.get("token").toString())
    }
    return p
  }
}

class Participant(_firstName:String, _lastName:String = "", _email:String = "", _language:String = "") {
	private var blacklisted:Boolean = false
	private var email:String = _email
	private var language:String = _language
	private var firstName:String = _firstName
	private var lastName:String = _lastName
	private var token:String = ""
	private var id:Integer = 0
	
	def setToken(_token:String) {
	  token = _token
	}
	
	def setId(_id:Integer) {
	  id = _id
	}
	
	def generateToken(): String = {
	  UUID.randomUUID().toString()
	}
	
	def activateBlacklisted() {blacklisted = true}
	def deactivateBlacklisted() {blacklisted = false}
	def setLanguage(lang:String) {language = lang}
	def setFirstName(name:String) {firstName = name}
	def setLastName(name:String) {lastName = name}
	def setEmail(nemail:String) {email = nemail}
	def getBlacklisted(): Boolean = {blacklisted}
	def getFirstName(): String = {firstName}
	def getLastName(): String = {lastName}
	def getEmail(): String = {email}
	def getLanguage(): String = {language}
	def getToken(): String = {token}
	def getId(): Integer = {id}
}