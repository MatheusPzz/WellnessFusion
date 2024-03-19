package com.example.wellnessfusionapp.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wellnessfusionapp.Models.Exercise
import com.example.wellnessfusionapp.Models.ExerciseDetail
import com.example.wellnessfusionapp.Models.Goal
import com.example.wellnessfusionapp.Models.Instructions
import com.example.wellnessfusionapp.Models.Notes
import com.example.wellnessfusionapp.Models.ProgressRecord
import com.example.wellnessfusionapp.Models.TrainingLog
import com.example.wellnessfusionapp.Models.WorkoutPlan
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    init {
        Log.d("MainViewModel", "MainViewModel created")
        fetchUserWorkoutPlans()
        Log.d("MainViewModel", "MainViewModel created")
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

//    fun saveNotesForUser(exerciseId: String, noteText: String) {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
//        val note = Notes(
//            userId = userId,
//            exerciseId = exerciseId,
//            noteText = noteText,
//            timestamp = System.currentTimeMillis()
//        )
//
//        FirebaseFirestore.getInstance()
//            .collection("Users").document(userId)
//            .collection("UserProfile").document(userId)
//            .collection("Notes").add(note)
//            .addOnSuccessListener {
//                Log.d("AddNote", "Note added successfully")
//                fetchNotesForUser(exerciseId) // Fetch updated notes list after adding new note
//            }
//            .addOnFailureListener { e ->
//                Log.e("AddNote", "Error adding note", e)
//            }
//    }
//
//    fun fetchNotesForUser(exerciseId: String) {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
//
//        FirebaseFirestore.getInstance()
//            .collection("Users").document(userId)
//            .collection("UserProfile").document(userId)
//            .collection("Notes")
//            .whereEqualTo("exerciseId", exerciseId)
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .addSnapshotListener { value, error ->
//                if (error != null) {
//                    Log.w("FetchNotes", "Listen failed.", error)
//                    return@addSnapshotListener
//                }
//                val notesList = value?.documents?.mapNotNull { doc ->
//                    doc.toObject(Notes::class.java)
//                } ?: emptyList()
//
//                // Atualiza o mapa de notas, mantendo as existentes e adicionando/atualizando as deste exerciseId
//                _notes.value = _notes.value.orEmpty() + (exerciseId to notesList)
//            }
//    }


    // funcao para adicao de logs para usuario, aqui vamos fazer fetching dos planos criados


    val _workoutPlans = MutableLiveData<List<WorkoutPlan>>()
    val workoutPlans: LiveData<List<WorkoutPlan>> = _workoutPlans

//    private fun getCurrentUserId(): String {
//        return FirebaseAuth.getInstance().currentUser?.uid.toString()
//
//    }

    fun fetchUserWorkoutPlans() {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
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
    fun clearSelectedWorkoutPlans() {
        _workoutPlans.value = emptyList()
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

    fun saveExerciseLog(
        logName: String,
        workoutPlanId: String,
        exerciseLogs: List<ExerciseDetail>
    ) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val logEntry = TrainingLog(
                logName = logName,
                logDate = Date(),
                workoutPlanId = workoutPlanId,
                exercises = exerciseLogs
            )

            try {
                Firebase.firestore
                    .collection("Users").document(userId)
                    .collection("UserProfile").document(userId)
                    .collection("TrainingLogs").add(logEntry)
                    .addOnSuccessListener { documentReference ->
                        Log.d("Save Log", "Training log saved successfully")

                        // Após salvar o log com sucesso, chamar a função para atualizar as metas
                        updateGoalsBasedOnLog(exerciseLogs)
                    }
                    .addOnFailureListener { e ->
                        Log.e("Save Log", "Error saving training log", e)
                    }
            } catch (e: Exception) {
                Log.e("Save Log", "Error saving training log", e)
            }
        }
    }

    private fun updateGoalsBasedOnLog(exerciseLogs: List<ExerciseDetail>) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

            val userGoalsRef = Firebase.firestore.collection("Users").document(userId)
                .collection("UserProfile").document(userId)
                .collection("Goals")

            // Removido o filtro incorreto, vamos buscar todas as metas e filtrar localmente.
            userGoalsRef.get().addOnSuccessListener { goalsSnapshot ->
                goalsSnapshot.documents.forEach { goalDocument ->
                    val goal = goalDocument.toObject(Goal::class.java) ?: return@forEach

                    exerciseLogs.forEach { log ->
                        if (log.exerciseId == goal.exerciseId && log.weight > goal.currentValue) {
                            // Atualiza a meta somente se o peso do log for maior que o currentValue da meta.
                            val newCurrentValue = log.weight
                            userGoalsRef.document(goal.id).update("currentValue", newCurrentValue)
                                .addOnSuccessListener {
                                    Log.d("UpdateGoal", "Goal updated successfully based on new log")
                                    Log.d("UpdateGoal", "New value: $newCurrentValue")
                                    fetchProgressHistory(goal.id)
                                }
                        }
                    }
                }
            }.addOnFailureListener { e ->
                Log.e("UpdateGoal", "Error fetching goals", e)
            }
        }
    }


    fun updateGoal(updatedGoal: Goal) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        // Preparar os dados a serem atualizados
        val goalUpdates = mapOf(
            "currentValue" to updatedGoal.currentValue,
            // Inclua outros campos que possam ser atualizados, se necessário
        )

        // Atualizar o documento correspondente na coleção de metas
        Firebase.firestore.collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("Goals").document(updatedGoal.id)
            .update(goalUpdates)
            .addOnSuccessListener {
                Log.d("UpdateGoal", "Goal successfully updated")
                saveProgressUpdate(updatedGoal.id, updatedGoal.currentValue.toFloat())
                // Aqui você pode adicionar qualquer lógica adicional após a atualização bem-sucedida,
                // como notificar o usuário ou atualizar a UI.
            }
            .addOnFailureListener { e ->
                Log.e("UpdateGoal", "Error updating goal", e)
                // Tratar o caso de erro, como informar o usuário da falha na atualização.
            }
    }

    private val _progressRecords = MutableStateFlow<List<ProgressRecord>>(emptyList())
    val progressRecords: StateFlow<List<ProgressRecord>> = _progressRecords.asStateFlow()

    fun fetchProgressHistory(goalId: String) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            Firebase.firestore.collection("Users").document(userId)
                .collection("UserProfile").document(userId)
                .collection("Goals").document(goalId)
                .collection("ProgressHistory")
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("fetchProgressHistory", "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val progressList = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(ProgressRecord::class.java)?.apply {
                            date = doc.getTimestamp("date") ?: Timestamp.now()
                        }
                    } ?: emptyList()

                    _progressRecords.value = progressList
                }
        }
    }

    fun saveProgressUpdate(goalId: String, newValue: Float) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val progressUpdate = mapOf(
            "date" to Timestamp.now(),
            "value" to newValue
        )

        Firebase.firestore.collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("Goals").document(goalId)
            .collection("ProgressHistory").add(progressUpdate)
            .addOnSuccessListener {
                Log.d("ProgressUpdate", "Progress history updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ProgressUpdate", "Error updating progress history", e)
            }
    }


    private val _savedLogs = MutableLiveData<List<TrainingLog>>()
    val savedLogs: LiveData<List<TrainingLog>> = _savedLogs


    private val _isAddingNewLog = MutableLiveData<Boolean>(false)
    val isAddingNewLog: LiveData<Boolean> = _isAddingNewLog

    fun startAddingNewLog() {
        _isAddingNewLog.value = true
    }

    fun finishAddingNewLog() {
        _isAddingNewLog.value = false
        // Opcionalmente, recarregue os logs existentes
        fetchSavedLogs()
    }

    fun toggleLogDetails(logName: String) {
        val updatedLogs = _savedLogs.value?.map { log ->
            if (log.logName == logName) {
                // Log para depuração
                Log.d("ToggleLogDetails", "Toggling visibility for log: $logName")
                log.copy(isDetailsVisible = !log.isDetailsVisible)
            } else {
                log
            }
        }
        _savedLogs.postValue(updatedLogs!!)
    }




// Deletar e editar logs

    fun deleteLog(logName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                // Reference to the TrainingLogs collection for the current user
                val logsRef = Firebase.firestore.collection("Users").document(userId)
                    .collection("UserProfile").document(userId)
                    .collection("TrainingLogs") // Corrected collection path

                // Query to find the specific log by name
                val querySnapshot = logsRef.whereEqualTo("logName", logName).get().await()

                // Attempt to find the first document that matches the query
                val logToDelete = querySnapshot.documents.firstOrNull()

                logToDelete?.let {
                    // If a matching document is found, delete it
                    logsRef.document(it.id).delete().await()
                    Log.d("DeleteLog", "Log deleted successfully")
                    fetchSavedLogs()
                }
            } catch (e: Exception) {
                Log.e("DeleteLog", "Error deleting log", e)
            }
        }
    }

    //Goals System

    private val _goals = MutableLiveData<List<Goal>>()
    val goals: LiveData<List<Goal>> = _goals

    init {
        fetchUserGoals()
    }

    fun fetchSavedLogs() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val logsList = mutableListOf<TrainingLog>()
            Firebase.firestore
                .collection("Users").document(userId)
                .collection("UserProfile").document(userId)
                .collection("TrainingLogs")
                .get()
                .await()
                .documents.forEach { document ->
                    val logName = document.getString("logName") ?: ""
                    val logDate = (document.getTimestamp("logDate")?.toDate() ?: Date())
                    val workoutPlanId = document.getString("workoutPlanId") ?: ""
                    val exercisesList =
                        document.get("exercises") as? List<Map<String, Any>> ?: emptyList()
                    val exercisesDetails = exercisesList.map { exerciseMap ->
                        ExerciseDetail(
                            exerciseId = exerciseMap["exerciseId"] as? String ?: "",
                            exerciseName = exerciseMap["exerciseName"] as? String ?: "",
                            sets = (exerciseMap["sets"] as? Long)?.toInt() ?: 0,
                            reps = (exerciseMap["reps"] as? Long)?.toInt() ?: 0,
                            weight = (exerciseMap["weight"] as? Double)?.toFloat() ?: 0f
                        )
                    }
                    logsList.add(
                        TrainingLog(
                            logName = logName,
                            logDate = logDate,
                            workoutPlanId = workoutPlanId,
                            exercises = exercisesDetails,
                            isDetailsVisible = false
                        )
                    )
                }
            _savedLogs.value = logsList
        }
    }
    fun addGoal(goal: Goal) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val goalMap = mapOf(
            "id" to goal.id,
            "typeId" to goal.type.id, // Store only the ID of the goal type
            "description" to goal.description,
            "desiredValue" to goal.desiredValue,
            "currentValue" to goal.currentValue,
            "exerciseId" to goal.exerciseId,
            "startDate" to goal.startDate,
            "endDate" to goal.endDate,
            "status" to goal.status,
            "workoutDays" to goal.workoutDays
        )

        Firebase.firestore.collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("Goals").document(goal.id).set(goalMap)
            .addOnSuccessListener {
                Log.d("AddGoal", "Goal added successfully")
                fetchUserGoals()
            }
            .addOnFailureListener { e ->
                Log.e("AddGoal", "Error adding goal", e)
            }
    }

    private fun fetchUserGoals() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Firebase.firestore.collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("Goals")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("FetchGoals", "Error fetching goals", error)
                    return@addSnapshotListener
                }

                val goalsList = value?.mapNotNull { it.toObject(Goal::class.java) } ?: listOf()
                _goals.value = goalsList
            }
    }


    private val _exercisesForDropdown = MutableLiveData<List<Exercise>>()
    val exercisesForDropdown: LiveData<List<Exercise>> = _exercisesForDropdown

    fun fetchAllExercises() {
        viewModelScope.launch(Dispatchers.IO) {
            val exerciseList = mutableListOf<Exercise>()
            try {
                // Fetch all documents in the "Exercises" collection
                val querySnapshot = Firebase.firestore.collection("Exercises").get().await()
                for (document in querySnapshot.documents) {
                    // Convert each document to an Exercise object
                    val exercise = document.toObject(Exercise::class.java)
                    exercise?.let { exerciseList.add(it) }
                }
                withContext(Dispatchers.Main) {
                   Log.d("ViewModel", "Exercises fetched successfully")
                    Log.d("ViewModel", "Fetched ${exerciseList.size} exercises")
                    _exercisesForDropdown.value = exerciseList
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ViewModel", "Error fetching exercises: ${e.message}")
                    _exercisesForDropdown.value = emptyList()
                }
            }
        }
    }

    private val _exerciseSpecificLogs = MutableLiveData<List<TrainingLog>>()
    val exerciseSpecificLogs: LiveData<List<TrainingLog>> = _exerciseSpecificLogs

    fun fetchLogsForExerciseGoals(exerciseId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val logsList = mutableListOf<TrainingLog>()
            Firebase.firestore
                .collection("Users").document(userId)
                .collection("UserProfile").document(userId)
                .collection("TrainingLogs")
                .whereArrayContains("exercises.exerciseId", exerciseId) // Esta linha é um exemplo e pode precisar de ajuste.
                .get()
                .await()
                .documents.forEach { document ->
                    val log = document.toObject(TrainingLog::class.java)
                    log?.let { logsList.add(it) }
                }
            _exerciseSpecificLogs.value = logsList // Assuma que _exerciseSpecificLogs é um LiveData ou StateFlow
        }
    }


}




    // Goals System

//    private val _userGoals = MutableLiveData<List<Goal>>(emptyList())
//
//    val userGoals: LiveData<List<Goal>> = _userGoals
//
//    fun fetchUserGoals() {
//        val goalsList = listOf(
//            Goal(type = "Número de Treinos", desiredValue = 10),
//            Goal(type = "Progressão de Carga", currentValue = 20, desiredValue = 50)
//        )
//        _userGoals.postValue(goalsList) // Use postValue if there's any chance of background thread usage
//    }
//
//
//    fun addUserGoal(newGoal: Goal){
//        val currentGoals = _userGoals.value ?: listOf()
//        _userGoals.value = currentGoals + newGoal
//
//    }
//
//    fun removeUserGoal(goalType: String) {
//        val updatedGoals = _userGoals.value?.filter { it.type != goalType }
//        _userGoals.value = updatedGoals!!
//    }
//
//    fun updateGoalProgress(goalId: String, progressIncrement: Int){
//        val updatedGoals = _userGoals.value?.map { goal ->
//            if(goal.id == goalId){
//                goal.copy(currentValue = goal.currentValue + progressIncrement)
//                    .also { updatedGoal ->
//                        if(updatedGoal.currentValue >= updatedGoal.desiredValue) {
//                            updatedGoal.copy(status = "completed")
//                        } else {
//                            updatedGoal
//                        }
//                    }
//            } else {
//                goal
//            }
//        } ?: emptyList()
//        _userGoals.value = updatedGoals
//    }
//
//    fun checkGoalsAchieved(): List<Goal> {
//        return _userGoals.value?.filter { it.status == "completed" } ?: emptyList()
//    }
//
//}


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
