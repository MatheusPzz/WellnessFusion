package com.example.wellnessfusionapp.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.ExerciseLog
import com.example.wellnessfusionapp.Models.Instructions
import com.example.wellnessfusionapp.Models.Notes
import com.example.wellnessfusionapp.Models.WorkoutPlan
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.type.Date
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    init {
        fetchUserWorkoutPlans()
        fetchSavedLogs()
    }

    private val _instructions = MutableLiveData<Instructions>()
    val instructions: LiveData<Instructions> = _instructions

    private val _notes = MutableLiveData<Map<String, List<Notes>>>(emptyMap())
    val notes: LiveData<Map<String, List<Notes>>> = _notes

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
            }.firstOrNull()

            _instructions.value = instructions!!
        }
    }

    fun saveNotesForUser(exerciseId: String, noteText: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val note = Notes(
            userId = userId,
            exerciseId = exerciseId,
            noteText = noteText,
            timestamp = System.currentTimeMillis()
        )

        FirebaseFirestore.getInstance()
            .collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("Notes").add(note)
            .addOnSuccessListener {
                Log.d("AddNote", "Note added successfully")
                fetchNotesForUser(exerciseId) // Fetch updated notes list after adding new note
            }
            .addOnFailureListener { e ->
                Log.e("AddNote", "Error adding note", e)
            }
    }

    fun fetchNotesForUser(exerciseId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("Notes")
            .whereEqualTo("exerciseId", exerciseId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.w("FetchNotes", "Listen failed.", error)
                    return@addSnapshotListener
                }
                val notesList = value?.documents?.mapNotNull { doc ->
                    doc.toObject(Notes::class.java)
                } ?: emptyList()

                // Atualiza o mapa de notas, mantendo as existentes e adicionando/atualizando as deste exerciseId
                _notes.value = _notes.value.orEmpty() + (exerciseId to notesList)
            }
    }


    // funcao para adicao de logs para usuario, aqui vamos fazer fetching dos planos criados


    val _workoutPlans = MutableLiveData<List<WorkoutPlan>>()
    val workoutPlans: LiveData<List<WorkoutPlan>> = _workoutPlans

    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("Usuário não está logado")
    }

    fun fetchUserWorkoutPlans() {
        viewModelScope.launch {
            try {
                val userId = getCurrentUserId() // Pode lançar IllegalStateException
                val db = Firebase.firestore

                val workoutPlan = db
                    .collection("Users").document(userId)
                    .collection("UserProfile").document(userId)
                    .collection("WorkoutPlans")
                    .get()
                    .await()
                    .documents.mapNotNull { it.toObject(WorkoutPlan::class.java) }

                _workoutPlans.value = workoutPlan
            } catch (e: IllegalStateException) {
                Log.e("WorkoutPlans", "Usuário não está logado.", e)
            } catch (e: Exception) {
                Log.e("WorkoutPlans", "Error fetching workout plans", e)
            }
        }
    }

    fun fetchExercisesDetailsByIds(
        exerciseIds: List<String>,
        onExercisesFetched: (List<Exercise>) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val exercises = mutableListOf<Exercise>()
            exerciseIds.forEach { id ->
                val exercise = Firebase.firestore.collection("Exercises")
                    .whereIn("id", listOf(id)).get().await().documents.firstOrNull()
                    ?.toObject(Exercise::class.java)
                exercise?.let { exercises.add(it) }
            }
            withContext(Dispatchers.Main) {
                onExercisesFetched(exercises)
            }
        }
    }

    fun saveExerciseLog(logName: String, workoutPlanId: String, exerciseLogs: List<ExerciseLog>) {
        viewModelScope.launch {
            val userId = getCurrentUserId()
            val logEntry = hashMapOf(
                "logName" to logName,
                "workoutPlanId" to workoutPlanId,
                "logs" to exerciseLogs,
                "date" to Timestamp.now()
            )


            try {
                Firebase.firestore.collection("Users").document(userId)
                    .collection("ExerciseLogs").add(logEntry)
                Log.d("Save Log", "Log saved successfully")
                fetchSavedLogs()
            } catch (e: Exception) {
                Log.e("Save Log", "Error saving log", e)
            }

        }
    }

    private val _savedLogs = MutableLiveData<List<ExerciseLog>>()
    val savedLogs: LiveData<List<ExerciseLog>> = _savedLogs

    fun toggleLogDetails(logId: String) {
        _savedLogs.value = _savedLogs.value?.map { log ->
            if (log.logName == logId) log.copy(isDetailsVisible = !log.isDetailsVisible) else log
        }
    }

    fun fetchSavedLogs() {
        val userId = getCurrentUserId()
        viewModelScope.launch {
            val logsList = mutableListOf<ExerciseLog>()
            Firebase.firestore.collection("Users").document(userId)
                .collection("ExerciseLogs")
                .get()
                .await()
                .documents.forEach { document ->
                    // Supondo que "logs" seja um campo do tipo lista dentro do documento
                    val logs = document.get("logs") as? List<Map<String, Any>> ?: emptyList()
                    logs.forEach { logMap ->
                        val logDate = (logMap["logDate"] as? com.google.firebase.Timestamp)?.toDate() ?: java.util.Date()
                        logsList.add(
                            ExerciseLog(
                                logName = document.getString("logName") ?: "",
                                logDate = logDate,
                                exerciseId = logMap["exerciseId"] as String,
                                exerciseName = logMap["exerciseName"] as String,
                                sets = (logMap["sets"] as Long).toInt(),
                                reps = (logMap["reps"] as Long).toInt(),
                                weight = (logMap["weight"] as Double).toFloat(),
                                isDetailsVisible = false
                            )
                        )
                    }
                }
            _savedLogs.value = logsList
        }
    }
}



//    fun uploadImageToFirebaseStorage(imageUri: Uri, onSuccess: (String) -> Unit) {
//        val userId = Firebase.auth.currentUser?.uid ?: return
//        val storageRef = Firebase.storage.reference.child("profile_picture/$userId.jpg")
//
//        storageRef.putFile(imageUri).continueWithTask { task ->
//            if (!task.isSuccessful) task.exception?.let { throw it }
//            storageRef.downloadUrl
//        }.addOnSuccessListener { uri ->
//            val imageUrl = uri.toString()
//            onSuccess(imageUrl)
//        }
//    }
//    fun updateProfilePictureInFirestore(imageUrl: String) {
//        val userId = Firebase.auth.currentUser?.uid ?: return
//        Firebase.firestore.collection("users").document(userId)
//            .update("profile_picture", imageUrl)
//            .addOnSuccessListener {
//
//            }
//    }
