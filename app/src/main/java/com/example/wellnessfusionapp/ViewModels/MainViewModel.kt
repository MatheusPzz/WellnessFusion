package com.example.wellnessfusionapp.ViewModels

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@SuppressLint("NewApi")
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    init {
        fetchUserWorkoutPlans()
        fetchSavedLogs()
        fetchUserProfilePicture()
        fetchCompletedGoals()
        fetchUserGoals()
    }




    // Fetching instructions for exercises, using the exercise ID as a parameter
    private val _instructions = MutableLiveData<Instructions>()
    val instructions: LiveData<Instructions> = _instructions

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



    // Fetching user workout plans from Firestore and storing the result in a LiveData object
    val _workoutPlans = MutableLiveData<List<WorkoutPlan>>()
    val workoutPlans: LiveData<List<WorkoutPlan>> = _workoutPlans

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



    // Fetching exercises details by their IDs and returning the result in a callback
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


    // Saving a new workout log record
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

            FirebaseFirestore.getInstance().collection("Users").document(userId)
                .collection("UserProfile").document(userId)
                .collection("TrainingLogs").add(logEntry)
                .addOnSuccessListener {
                    Log.d("Save Log", "Training log saved successfully")
                    Log.d("SaveLog", "Saving log with details: $exerciseLogs")
                    // Chamar a função para atualizar as metas
                    updateGoalsBasedOnLog(exerciseLogs, userId)
                    fetchSavedLogs()
                }
                .addOnFailureListener { e ->
                    Log.e("Save Log", "Error saving training log", e)
                }
        }
    }

    // Updating the information from database based on a user input log
    private fun updateGoalsBasedOnLog(exerciseLogs: List<ExerciseDetail>, userId: String) {
        val userGoalsRef = FirebaseFirestore.getInstance().collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("Goals")

        userGoalsRef.get().addOnSuccessListener { goalsSnapshot ->
            goalsSnapshot.documents.forEach { goalDocument ->
                val goal = goalDocument.toObject(Goal::class.java) ?: return@forEach

                exerciseLogs.forEach { log ->
                    if (log.exerciseId == goal.exerciseId && log.weight > goal.currentValue) {
                        val newCurrentValue = log.weight
                        userGoalsRef.document(goal.id).update("currentValue", newCurrentValue)
                            .addOnSuccessListener {
                                Log.d("UpdateGoal", "Goal updated successfully based on new log")
                                // Atualizar o histórico de progresso aqui garante que aconteça após a meta ser atualizada
                                saveProgressUpdate(goal.id, newCurrentValue)
                                checkAndCompleteGoal(goal.id) // Novo: Verificar se a meta foi completada

                            }
                    }
                }
            }
        }.addOnFailureListener { e ->
            Log.e("UpdateGoal", "Error fetching goals", e)
        }
    }


    // Updating a goal document in firestore
    fun updateGoal(updatedGoal: Goal) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("Goals").document(updatedGoal.id)
            .update(mapOf("currentValue" to updatedGoal.currentValue))
            .addOnSuccessListener {
                Log.d("UpdateGoal", "Goal successfully updated")
                // Após a atualização da meta, salvar o progresso
                saveProgressUpdate(updatedGoal.id, updatedGoal.currentValue)
            }
            .addOnFailureListener { e ->
                Log.e("UpdateGoal", "Error updating goal", e)
            }
    }

    // Getting information about the a week in general
    private val _currentWeek = MutableStateFlow(1) // Default to week 1
    val currentWeek: StateFlow<Int> = _currentWeek.asStateFlow()

    fun setWeek(week: Int) {
        _currentWeek.value = week
    }


    // Fetching the progress history of a goal update in Firestore
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


    // Saving the progress of a goal in firestore
    fun saveProgressUpdate(goalId: String, newValue: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val progressUpdate = mapOf(
            "date" to Timestamp.now(),
            "value" to newValue
        )

        FirebaseFirestore.getInstance().collection("Users").document(userId)
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


    // Live state for the dialog to add a new log
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

    fun getTrainingLog(logName: String): TrainingLog? {
        return _savedLogs.value?.find { it.logName == logName }
    }


    // Delete function for a log / not used anymore but it was implemented, deleting a firestore document

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


    // Fetching the user saved logs from firestore
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
                            weight = (exerciseMap["weight"] as? Long)?.toInt() ?: 0
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

    // Add a goal to the firestore database

    fun addGoal(goal: Goal) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val goalMap = mapOf(
            "id" to goal.id,
            "typeId" to goal.type.name, // Store only the ID of the goal type
            "description" to goal.description,
            "desiredValue" to goal.desiredValue,
            "initialValue" to goal.initialValue,
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



    // Fetching current user goals from firestore
    private fun fetchUserGoals() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        Firebase.firestore.collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("Goals")
            .whereEqualTo("status", "active") // Adicionado filtro para status ativo
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("FetchGoals", "Error fetching goals", error)
                    return@addSnapshotListener
                }

                val activeGoalsList =
                    value?.mapNotNull { it.toObject(Goal::class.java) } ?: listOf()
                _goals.value = activeGoalsList
            }
    }



    // Fetching all exercises from the exercises collection and adding it to a list for the dropdown list of adding goals
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


    // Check and complete goal based on log input.

    private val _goalCompletionEvent = MutableLiveData<Goal?>()
    val goalCompletionEvent: LiveData<Goal?> = _goalCompletionEvent

    fun clearGoalCompletionEvent() {
        _goalCompletionEvent.value = null
    }


    // Check and complete the goal document in firestore, changing the string status and date
    private fun checkAndCompleteGoal(goalId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val goalRef = Firebase.firestore
                    .collection("Users").document(userId)
                    .collection("UserProfile").document(userId)
                    .collection("Goals").document(goalId)

                // Use await() from kotlinx.coroutines.tasks.await to wait for the get() operation
                val documentSnapshot = goalRef.get().await()
                val goal = documentSnapshot.toObject(Goal::class.java)

                goal?.let {
                    if (it.currentValue >= it.desiredValue && it.status != "completed") {
                        // Goal met, update status and end date
                        val updates = hashMapOf<String, Any>(
                            "status" to "completed",
                            "endDate" to FieldValue.serverTimestamp() // Use serverTimestamp for consistency
                        )
                        goalRef.update(updates).addOnSuccessListener {
                            Log.d("GoalStatus", "Goal successfully marked as completed")
                            // Assuming _goalCompletionEvent is a LiveData or similar observable
                            _goalCompletionEvent.postValue(goal)
                        }.addOnFailureListener { e ->
                            Log.e("GoalStatus", "Error updating goal status", e)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GoalStatus", "Error checking/completing goal", e)
            }
        }
    }

    // Fetching the completed goals from firestore
    private val _completedGoals = MutableLiveData<List<Goal>>()
    val completedGoals: MutableLiveData<List<Goal>> = _completedGoals
    private fun fetchCompletedGoals() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            Firebase.firestore.collection("Users").document(userId)
                .collection("UserProfile").document(userId)
                .collection("Goals")
                .whereEqualTo("status", "completed")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.e("FetchCompletedGoals", "Error fetching completed goals", error)
                        return@addSnapshotListener
                    }


                    val completedGoalsList =
                        value?.mapNotNull { it.toObject(Goal::class.java) } ?: listOf()
                    _completedGoals.postValue(completedGoalsList)
                }
        }
    }



    // Mock for fetching the logs for exercise goals / But not needed, i will leave it for academic purposes
//    fun fetchLogsForExerciseGoals(exerciseId: String) {
//        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
//        viewModelScope.launch {
//            val logsList = mutableListOf<TrainingLog>()
//            Firebase.firestore
//                .collection("Users").document(userId)
//                .collection("UserProfile").document(userId)
//                .collection("TrainingLogs")
//                .whereArrayContains("exercises.exerciseId", exerciseId) // Esta linha é um exemplo e pode precisar de ajuste.
//                .get()
//                .await()
//                .documents.forEach { document ->
//                    val log = document.toObject(TrainingLog::class.java)
//                    Log.d("FetchLogs", "Fetched log: $log")
//                    log?.let { logsList.add(it) }
//                }
//            _exerciseSpecificLogs.value = logsList // Assuma que _exerciseSpecificLogs é um LiveData ou StateFlow
//            Log.d("FetchLogs", "Fetched ${logsList.size} logs for exercise $exerciseId")
//        }
//    }


    // Fetching the logs for exercise goals from firestore, not used, replaced by the function
    fun fetchLogsForExerciseGoals(exerciseId: String) {
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
                    val log = document.toObject(TrainingLog::class.java)
                    // Ajuste na depuração para mostrar IDs dos exercícios corretamente
                    log?.exercises?.let { exercises ->
                        val exerciseIds = exercises.map { it.exerciseId }
                        Log.d("FetchLogs", "Exercises in log: ${exerciseIds.joinToString()}")
                    }
                    // Filtragem correta dos logs que contêm o exerciseId específico
                    if (log?.exercises?.any { it.exerciseId == exerciseId } == true) {
                        logsList.add(log)
                    }
                }
            _exerciseSpecificLogs.postValue(logsList)
            Log.d("FetchLogs", "Fetched ${logsList.size} logs for exercise $exerciseId")
        }
    }

    // Updating the profile picture of the user using a firestore storage method to save the new picture
    private val _profilePictureUrl = MutableStateFlow<String?>(null)
    val profilePictureUrl = _profilePictureUrl.asStateFlow()
    fun updateUserProfilePicture(
        imageUri: Uri,
        userId: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("profile_pictures/$userId.jpg")
        val uploadTask = storageRef.putFile(imageUri)
        if (FirebaseAuth.getInstance().currentUser == null) {
            onComplete(false, "No authenticated user.")
            return
        }

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                val db = FirebaseFirestore.getInstance()
                db.collection("Users").document(userId)
                    .update("profile_picture", downloadUri.toString())
                    .addOnSuccessListener {
                        onComplete(true, "Profile picture updated successfully.")
                        fetchUserProfilePicture()
                    }
                    .addOnFailureListener { e ->
                        onComplete(false, e.message ?: "Error updating profile picture.")
                    }
            } else {
                onComplete(false, task.exception?.message ?: "Error uploading profile picture.")
            }
        }
    }


    // Fetching newly updated profile picture from firestore
    fun fetchUserProfilePicture() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("Users").document(userId).get()
            .addOnSuccessListener { document ->
                val profilePictureUrl = document.getString("profile_picture")
                _profilePictureUrl.value = profilePictureUrl
            }
            .addOnFailureListener { e ->
                Log.e("FetchProfilePicture", "Error fetching profile picture", e)
            }
    }

    private val _userName = MutableStateFlow("") // Default value
    val userName: StateFlow<String> = _userName.asStateFlow()
    suspend fun fetchUserName() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        try {
            Log.d("Firestore", "Fetching user name for ID: $userId")
            val documentSnapshot = db.collection("Users").document(userId).get().await()
            val fetchedName =
                documentSnapshot.getString("name") // Use a different variable name here
            Log.d("Firestore", "Fetched name: $fetchedName")

            // Update the _userName StateFlow
            _userName.value = fetchedName ?: "User Not Found"
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching user name", e)
            _userName.value = "Error Fetching User" // Update StateFlow on error
        }
    }


    // Updating the user name in firestore method and storing the result in variable passed to the UI
    private val _updateResult = MutableLiveData<Pair<Boolean, String>>()
    val updateResult: LiveData<Pair<Boolean, String>> = _updateResult

    fun updateUserName(userId: String, newName: String) {
        if (FirebaseAuth.getInstance().currentUser == null) {
            _updateResult.value = Pair(false, "No authenticated user.")
            return
        }

        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance().collection("Users").document(userId)
                    .update("name", newName)
                    .addOnSuccessListener {
                        _userName.value = newName // Assuming _userName is a MutableLiveData managing the username state
                        _updateResult.value = Pair(true, "Name updated successfully.")
                    }
                    .addOnFailureListener { exception ->
                        _updateResult.value = Pair(false, exception.message ?: "Error updating name.")
                    }
            } catch (e: Exception) {
                _updateResult.value = Pair(false, e.message ?: "Error updating name.")
            }
        }
    }


    // Fetching the goals details from firestore database
    private val _goalDetails = MutableLiveData<Goal>()
    val goalDetails: LiveData<Goal> = _goalDetails

    fun fetchGoalDetails(goalId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            Firebase.firestore
                .collection("Users").document(userId)
                .collection("UserProfile").document(userId)
                .collection("Goals").document(goalId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val goal = documentSnapshot.toObject(Goal::class.java)
                    goal?.let {
                        _goalDetails.postValue(it)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FetchGoalDetails", "Error fetching goal details", e)
                }
        }
    }





    // Completing a workout function, updating the workout plan in firestore
    fun completeWorkoutPlan(workoutPlanId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        if (userId.isEmpty() || workoutPlanId.isEmpty()) {
            Log.e("CompleteWorkoutPlan", "User ID or Plan ID is empty.")
            return
        }

        val planRef = Firebase.firestore
            .collection("Users").document(userId)
            .collection("UserProfile").document(userId)
            .collection("WorkoutPlans").document(workoutPlanId)

        Firebase.firestore.runTransaction { transaction ->
            val snapshot = transaction.get(planRef).toObject(WorkoutPlan::class.java)
            if (snapshot != null) {
                val updates = hashMapOf<String, Any>(
                    "isStarted" to true,
                    "completedDate" to Timestamp.now(),
                    "timesFinished" to snapshot.timesFinished + 1
                )
                transaction.update(planRef, updates)
            }
        }.addOnSuccessListener {
            Log.d("CompleteWorkoutPlan", "Workout plan $workoutPlanId simple update successful.")
        }.addOnFailureListener { e ->
            Log.e("CompleteWorkoutPlan", "Simple update failed.", e)
        }
    }

    // State management for the favorite exercises / not working, needs further investigation, but it was implemented
    private val _exercises = mutableStateListOf<Exercise>()
    val exercises: List<Exercise> = _exercises

    fun toggleFavoriteExercise(exercise: Exercise) {
        val index = _exercises.indexOfFirst { it.id == exercise.id }
        if (index != -1) {
            val updatedExercise = _exercises[index].copy(favorite = !exercise.favorite)
            _exercises[index] = updatedExercise
        }
    }
}

