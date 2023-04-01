package com.example.posturecorrectionapp.utils

import android.util.Log
import com.example.posturecorrectionapp.data.BodyPart
import com.example.posturecorrectionapp.data.Person
import com.example.posturecorrectionapp.data.PoseRule
import com.example.posturecorrectionapp.models.ExerciseRule
import java.io.InputStream

class ExerciseLogicUtils {
    // this class comes with utility functions to check if the user is doing the exercise correctly

    fun readAllExercise(
        rules: InputStream,
        exerciseNames: List<String?>
    ): HashMap<String, ExerciseRule> {
        // Read the exercise rules from the assets folder,
        if (exerciseNames.isEmpty()) {
            Log.d("ExerciseLogicUtils", "readAllExercise: exerciseNames is empty")
            return HashMap()
        }
        // and return a list of Exercise objects
//         var exerciseList = HashMap<String, Exercise>()
        var exerciseList = HashMap<String,ExerciseRule>()
        var lineNum = 0
        //read csv file
        rules.bufferedReader().useLines { lines ->
            // Skip the first line
            lines.forEach {
                lineNum += 1
                if (lineNum == 1) {
                    return@forEach
                }
                val line = it.split(",")
                val name = line[0]
                val pose = line[1]
                val ruleName = line[2]
                val point1 = line[3]
                val point2 = line[4]
                val point3 = line[5]
                val minAngle: Float = line[6].toFloat()
                val maxAngle: Float = line[7].toFloat()
                val leniency = 0.0f

                //convert string to enum
                val bodyPart1 = BodyPart.fromLabel(point1)
                val bodyPart2 = BodyPart.fromLabel(point2)
                val bodyPart3 = BodyPart.fromLabel(point3)

                val rule = PoseRule(
                    ruleName,
                    bodyPart1,
                    bodyPart2,
                    bodyPart3,
                    minAngle,
                    maxAngle,
                    leniency
                )
                Log.d(pose, "readAllExercise: $rule")

                // check if exerciseRule with the same name already exists in the list
                if (exerciseList.containsKey(name)) {
                    // If the exercise is already in the list
                    //retrieve the exerciseRule class
                    exerciseList[name]!!.addRule(pose, rule)
                } else {
                    exerciseList[name] = ExerciseRule(name, "", pose, rule)
                }
            }
        }
        for (names in exerciseNames) {
            if (!exerciseList.containsKey(names)) {
                exerciseList[names!!] = ExerciseRule(names, "", "", null)
            }
        }

        Log.d("ExerciseLogicUtils", "readAllExercise: $exerciseList")

        return exerciseList

    }

    fun checkPose(poseRules: List<PoseRule>, person: Person): Boolean {
        for (rule in poseRules) {
            if (!checkRule(rule, person)) {
                Log.d("ExerciseLogicUtils", "checkPose: ${rule.name} failed")
                return false
            }
        }
        return true
    }

    fun feedback(poseRules: List<PoseRule>, person: Person): String {
        for (rule in poseRules) {
            if (!checkRule(rule, person)) {
                return rule.name
            }
        }
        return "Good Job!"
    }

    private fun checkRule(rule: PoseRule, person: Person): Boolean {

        //Parse the rule and check if the user is doing the exercise correctly
        val bodyPart1 = rule.bodyPart1
        val bodyPart2 = rule.bodyPart2
        val bodyPart3 = rule.bodyPart3
        val minAngle = rule.minAngle
        val maxAngle = rule.maxAngle

        // Get the body part from the person object
        val bodyPart1Point = person.keyPoints[bodyPart1.ordinal]
        val bodyPart2Point = person.keyPoints[bodyPart2.ordinal]
        val bodyPart3Point = person.keyPoints[bodyPart3?.ordinal!!]

        return AngleCheckingUtils.check3PointAngle(
            bodyPart1Point,
            bodyPart2Point,
            bodyPart3Point,
            minAngle,
            maxAngle
        )
    }

    fun getRelevantPoseFromClassificationModel(
        poseLs: List<Pair<String, Float>>,
        exercise: ExerciseRule
    ): String {
        if (poseLs.isEmpty()) {
            return "None"
        }
        //get relevant pose from exercise
        val relevantPose = exercise.exerciseRule!!.keys
        //get the pose with the highest score
        var relevantClassification = ArrayList<Pair<String,Float>>()
        for (pose in poseLs) {
            if (relevantPose.contains(pose.first)) {
                relevantClassification.add(pose)
            }
        }
        Log.d( "ExerciseLogicUtils", "getRelevantPoseFromClassificationModel: $relevantClassification")
        if (relevantClassification.isEmpty()) {
            return "None"
        }
        var maxPose = relevantClassification[0].first
        var maxScore = relevantClassification[0].second
        for (pose in relevantClassification) {
            if (pose.second > maxScore) {
                maxScore = pose.second
                maxPose = pose.first
            }
        }
//        if (maxScore < 0.1) {
//            maxPose = "None"
//        }
        Log.d("ExerciseLogicUtils", "getRelevantPoseFromClassificationModel: $maxPose")
        return maxPose
    }

    fun getFeedbackFromPose(
        pose: String,
        exercise: ExerciseRule,
        person: Person
    ): String {
        //get the feedback from the pose
        val poseRules = exercise.exerciseRule!![pose]
        return feedback(poseRules!!, person)
    }

}