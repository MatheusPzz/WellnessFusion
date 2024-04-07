package com.example.wellnessfusionapp.Models

import com.google.firebase.Timestamp

data class ProgressRecord(
    var date: Timestamp,
    val value: Int
){
    constructor(): this(Timestamp.now(), 0)
}
