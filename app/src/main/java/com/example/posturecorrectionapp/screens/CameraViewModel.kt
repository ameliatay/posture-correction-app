package com.example.posturecorrectionapp.screens

import android.app.Person
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

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


    //Getter and Setter
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

    //Other functions
    // Start the timer for the current exercise
    fun startTimer(timeLeft: Int) {
        // Count down from and update the time left every second
        // When the time left is 0, stop the timer
        Thread {
            while (timeLeft > 0) {
                Thread.sleep(1000)
                setCurrentTimeLeft(timeLeft - 1)
            }
        }.start()
    }

    // Stop the timer for the current exercise
    fun stopTimer() {

    }


    //OnCleared
    override fun onCleared() {
        super.onCleared()
        // Stop the timer
        stopTimer()
    }

}