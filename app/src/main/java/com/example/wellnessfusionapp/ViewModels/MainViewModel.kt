package com.example.wellnessfusionapp.ViewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellnessfusionapp.Models.Instructions
import com.example.wellnessfusionapp.Models.Notes
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MainViewModel  @Inject constructor() : ViewModel() {

    val _instructions = MutableLiveData<Instructions>()
    val instructions = _instructions

    fun fetchInstructionsForExercise(exerciseId: String) {
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



    // Inserting Notes for each exercise user wants.

    fun saveNotesForUser(exerciseId: String, noteText: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val note = Notes(
            userId = userId,
            exerciseId = exerciseId,
            noteText = noteText,
            timestamp = Long.MAX_VALUE - System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance()
            .collection("Users").document(userId)
            .collection("UserProfile").document(userId) // Use o userID correto aqui
            .collection("Notes").add(note)
            .addOnSuccessListener {
                Log.d("AddNote", "Note added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("AddNote", "Error adding note", e)
            }
    }

    private val _notes = MutableLiveData<List<Notes>>()
    val notes = _notes
    fun fetchNotesForUser(exerciseId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("Notes").orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("FetchNotes", "Listen failed.", error)
                    return@addSnapshotListener
                }
                val notesList = mutableListOf<Notes>()
                for (doc in value!!) {
                    val note = doc.toObject(Notes::class.java)
                    notesList.add(note)
                }

                _notes.value = notesList
            }
    }
}