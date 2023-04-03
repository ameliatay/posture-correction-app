package com.example.posturecorrectionapp.models

import android.util.Log
import com.example.posturecorrectionapp.data.PoseRule

class ExerciseRule {
    var name: String? = null
    var description: String? = null
    var exerciseRule: HashMap<String, List<PoseRule>>

    constructor(name: String?, description: String?, exerciseRule: Map<String, List<PoseRule>>?) {
        this.name = name
        this.description = description
        this.exerciseRule = (exerciseRule ?: HashMap()) as HashMap<String, List<PoseRule>>
    }

    constructor(name: String?, description: String?, pose: String, rule: PoseRule?) {
        this.name = name
        this.description = description
        this.exerciseRule = HashMap()
        if (rule != null) {
            this.exerciseRule[pose] = listOf(rule)
        }
    }

    fun addRule(pose: String, rule: PoseRule) {
        Log.d("ExerciseRule", "addRule invoked")
        if (exerciseRule.containsKey(pose)) {
            val ruleList = this.exerciseRule[pose]!!.toMutableList()
            ruleList.add(rule)
            exerciseRule[pose] = ruleList.toList()
            Log.d("ExerciseRule", "posefound, addRule: $pose, $ruleList")
        } else {
            this.exerciseRule[pose] = listOf(rule)
            Log.d("ExerciseRule", "pose not found, addRule: $pose, $rule")
        }
        Log.d("ExerciseRule", "finished addRule: ${this.exerciseRule}}")
    }

    fun getRule(pose: String, ruleName: String): Array<PoseRule>{
        val rules = exerciseRule?.get(pose)
        val ruleList = mutableListOf<PoseRule>()
        for (rule in rules!!) {
            if (rule.name == ruleName) {
                ruleList.add(rule)
            }
        }
        return ruleList.toTypedArray()
    }

    override fun toString(): String {
        return "ExerciseRule(name=$name, description=$description, exerciseRule=${this.exerciseRule})"
    }

}