package com.ping.app.data.model

data class User(val name: String, val email:String, val region: String, val MeetingManagerUID:String){
    constructor():this("","","","")
}