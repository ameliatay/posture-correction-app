package com.example.posturecorrectionapp.models

import android.app.Person
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.posturecorrectionapp.data.Exercise
import java.lang.Thread.sleep

class CameraViewModel : ViewModel() {

    //StaticData
    private var exerciseLogic = HashMap<String, ExerciseRule> ()
    //LiveData
    // Store the current exercise
    private var currentExercise = MutableLiveData<String>()
    // Store the current repetition
    private var currentRepetition = MutableLiveData<Int>()
    // Store the current feedback
    private var currentFeedback = MutableLiveData<String>()
    // Store the current score
    private var currentScore = MutableLiveData<Int>()
    // Store the current peopleLandMarkInfo
    private var currentPeopleLandMarkLs = MutableLiveData<List<Person>>()
    // Store the current time left
    private var currentTimeLeft = MutableLiveData<Int>()
    //Store the state
    private var isTimerRunning = MutableLiveData<Boolean>()
    //Store the current pose
    private var currentPose = MutableLiveData<String>()
    //Store the boolean value of whether the exercise is finished
    private var curExerciseIndex = MutableLiveData<Int>()


    //Getter and Setter
    fun getExerciseLogic(): HashMap<String, ExerciseRule> {
        return exerciseLogic
    }

    fun setExerciseLogic(exerciseLogic: HashMap<String, ExerciseRule>) {
        this.exerciseLogic = exerciseLogic
    }

    fun getCurrentExercise(): MutableLiveData<String> {
        return currentExercise
    }

    fun setCurrentExercise(exercise: String) {
        currentExercise.value = exercise
    }

    fun getCurrentRepetition(): MutableLiveData<Int> {
        return currentRepetition
    }

    fun setCurrentRepetition(repetition: Int) {
        currentRepetition.value = repetition
    }

    fun getCurrentFeedback(): MutableLiveData<String> {
        return currentFeedback
    }

    fun setCurrentFeedback(feedback: String) {
        currentFeedback.value = feedback
    }

    fun getCurrentScore(): MutableLiveData<Int> {
        return currentScore
    }

    fun setCurrentScore(score: Int) {
        currentScore.value = score
    }

    fun getCurrentPeopleLandMarkLs(): MutableLiveData<List<Person>> {
        return currentPeopleLandMarkLs
    }

    fun setCurrentPeopleLandMarkLs(peopleLandMarkLs: List<Person>) {
        currentPeopleLandMarkLs.value = peopleLandMarkLs
    }

    fun getCurrentTimeLeft(): MutableLiveData<Int> {
        return currentTimeLeft
    }

    fun setCurrentTimeLeft(timeLeft: Int) {
        currentTimeLeft.value = timeLeft
    }

    fun getIsTimerRunning(): MutableLiveData<Boolean> {
        return isTimerRunning
    }

    fun setIsTimerRunning(state: Boolean) {
        isTimerRunning.value = state
    }

    fun getCurrentPose(): MutableLiveData<String> {
        return currentPose
    }

    fun setCurrentPose(pose: String) {
        currentPose.value = pose
    }

    fun addCount(feedback: String) {
        if (currentFeedback.value != feedback) {
            currentRepetition.value = currentRepetition.value?.plus(1)
            currentScore.value = currentScore.value?.plus(1)
        }

    }


    fun getCurExerciseIndex(): MutableLiveData<Int> {
        return curExerciseIndex
    }

    fun setCurExerciseIndex(index: Int) {
        curExerciseIndex.value = index
    }


    //Other functions

    //OnCleared
    override fun onCleared() {
        super.onCleared()
        //reset all the data
        Log.d("CameraViewModel", "onCleared: ")
    }

}