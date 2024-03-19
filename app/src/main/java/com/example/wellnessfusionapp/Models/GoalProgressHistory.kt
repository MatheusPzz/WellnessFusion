package com.example.wellnessfusionapp.Models

import com.google.firebase.Timestamp

data class ProgressRecord(
    var date: Timestamp, // ou Date, dependendo de como você deseja manipular datas
    val value: Int
){
    constructor(): this(Timestamp.now(), 0)
}
