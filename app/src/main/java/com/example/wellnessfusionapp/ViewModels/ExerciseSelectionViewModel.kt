package com.example.wellnessfusionapp.ViewModels


import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.wellnessfusionapp.Models.Category
import com.example.wellnessfusionapp.Models.Exercise
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@Suppress("IMPLICIT_CAST_TO_ANY")
@HiltViewModel
class ExerciseSelectionViewModel @Inject constructor() : ViewModel() {


    private val _groupedExercisesState = MutableStateFlow<Map<String, List<Exercise>>>(mapOf())
    val groupedExercisesState: StateFlow<Map<String, List<Exercise>>> =
        _groupedExercisesState.asStateFlow()


    init {
        listenToCategorySelectionChanges()
    }

    /* aqui estamos iniciando uma coleta de fluxo que escuta mudancas nas categorias selectionas, quando ha uma alteracao
    * ela chama a variabel update exercises com os novos ids de categoria selectionados*/


    private fun listenToCategorySelectionChanges() {
        viewModelScope.launch {
            CategoryViewModel.SharedCategorySelection.selectedCategoryIds.collect { selectedCategoryIds ->
                updateExercises(selectedCategoryIds)
            }
        }
    }

    /*
    Busca os exercicios do Firestore baseados nos ids das categorias selecionadas e atualiza o estado _groupedExercisesState
    com os exercicios agrupados por nome da categoria
    * */

    private suspend fun updateExercises(categoryIds: List<String>) {
        val exercises = getExercisesFromFireStore(categoryIds)
        _groupedExercisesState.value = exercises.groupBy { it.categoryName }
    }


    /*
    * Realiza uma consulta ao firestore para buscar exercicios que correspondem aos ids de categorias selecionadas
    * retorna uma lista de objetos exercicios
    * */

    private suspend fun getExercisesFromFireStore(categoryIds: List<String>): List<Exercise> {
        val db = FirebaseFirestore.getInstance()
        val exercises = mutableListOf<Exercise>()

        if (categoryIds.isNotEmpty()) {
            try {
                db.collection("Exercises")
                    .whereIn("categoryId", categoryIds)
                    .get()
                    .await()
                    .documents
                    .forEach { document ->
                        document.toObject(Exercise::class.java)?.let { exercise ->
                            exercises.add(exercise)
                        }
                    }
            } catch (e: Exception) {
                Log.e("ExerciseSelectionVM", "Error fetching exercises: ${e.message}", e)
            }
        }
        return exercises
    }

    /* aqui estou limpando as selecoes de exercicios que usuario fez ao voltar para pagina anterior*/

    private fun clearExercisesSelection(){
        _selectedExercises.value = emptyList()
    }

    /*o mesmo que acima estou limpando as categorias baseados na selecao de usuario ao voltar a pagina*/
    private fun clearCategorySelections() {
        viewModelScope.launch {
            CategoryViewModel.SharedCategorySelection.updateSelectedCategoryIds(emptyList())
        }
    }


    /* e aqui so estamos chamando as duas funcoes em uma para simplificar o processo de limpeza das selecoes*/
    fun clearAllSelections() {
        clearExercisesSelection()
        clearCategorySelections()
    }


// Exercise selection logic


    private val _selectedExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val selectedExercises: StateFlow<List<Exercise>> = _selectedExercises.asStateFlow()
    val currentSelection = _selectedExercises.value



    /*Adiciona ou remove um exercicio da lista de exercicios selecionados com base na presenca do exercicio na lista*/
    fun toggleExerciseSelection(exercise: Exercise) {

        val currentSelection = _selectedExercises.value.toMutableList()
        if (currentSelection.any { it.id == exercise.id })
        {
            currentSelection.removeAll { it.id == exercise.id }
        } else {
            currentSelection.add(exercise)
        }
        _selectedExercises.value = currentSelection
    }

    /*simente verificando se o um exercicio especifico esta selecionado*/
    fun isExerciseSelected(exercise: Exercise): Boolean {
        return _selectedExercises.value.any { it.id == exercise.id }
    }



    /*retornando o id do usuario que esta logado na sessao usando firebase auth*/
    fun getCurrentUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }


    /*salvando o plano de treino criado no firestore sob o perfil do usuario atual e navega para a tela "Home" em sucesso*/
    fun saveWorkoutPlan(navController: NavController, planName: String) {
        val userId = getCurrentUserId()

        if (userId != null) {
            val db = Firebase.firestore
            val planDocument = db.collection("Users").document(userId).collection("UserProfile").document(userId).collection("WorkoutPlans").document()

            // Use the _selectedExercises.value directly for the current exercise selection
            val exerciseData = _selectedExercises.value.map { exercise ->
                    exercise.id
            }

            val workoutPlan = hashMapOf(
                "planName" to planName,
                "exercises" to exerciseData, // Use the detailed exercise data here
                "creationDate" to Timestamp.now(),
                "workoutPlanId" to planDocument.id
            )

            planDocument.set(workoutPlan)
                .addOnSuccessListener {
                    Log.d("Save Workout Plan", "Saving workout plan with exercise IDs: $exerciseData")
                    Log.d("ExerciseSelectionVM", "Workout plan saved successfully")
                    navController.navigate("home")
                }
                .addOnFailureListener { e ->
                    Log.e("ExerciseSelectionVM", "Error saving workout plan: ${e.message}", e)
                }
        } else {
            Log.e("ExerciseSelectionVM", "User is not logged in")
        }
    }


    }


//    val response: MutableState<DataState<List<Exercise>>> = mutableStateOf(DataState.Loading)
//    init {
//        fetchDataFromDatabase()
//    }
//
//    private fun fetchDataFromDatabase() {
//        val tempList = mutableListOf<Exercise>()
//        val databaseReference = FirebaseDatabase.getInstance().getReference("Exercises")
//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                tempList.clear()
//                for (dataSnapshot in snapshot.children) {
//                    val exerciseItem = dataSnapshot.getValue(Exercise::class.java)
//                    exerciseItem?.let {
//                        tempList.add(it)
//                    }
//                }
//                response.value = DataState.Success(tempList)
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                response.value = DataState.Failure(error.message)
//            }
//        })
//    }
//    private fun getData() {
//        viewModelScope.launch {
//            state.value = getExercisesFromFireStore()
//
//
//        }
//    }
