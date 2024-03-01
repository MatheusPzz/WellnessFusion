package com.example.wellnessfusionapp.ViewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellnessfusionapp.Models.Instructions
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MainViewModel  @Inject constructor() : ViewModel(){

    val _instructions = MutableLiveData<Instructions>()
    val instructions = _instructions

    fun fetchInstructionsForExercise(exerciseId: String){
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val instructionQuery = db
                .collection("Instructions")
                .whereEqualTo("exerciseId", exerciseId)
                .get()
                .await()


            val instructions = instructionQuery.documents.mapNotNull { document ->
                document.toObject(Instructions::class.java)
            }

            _instructions.value = instructions.firstOrNull()
        }
    }


}