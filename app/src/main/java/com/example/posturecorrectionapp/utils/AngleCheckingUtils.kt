package com.example.posturecorrectionapp.utils

import android.util.Log
import com.example.posturecorrectionapp.data.BodyPart
import com.example.posturecorrectionapp.data.KeyPoint
import com.example.posturecorrectionapp.data.Person
import org.checkerframework.checker.units.qual.Angle
import kotlin.math.atan2

object AngleCheckingUtils {
//    fun checkAngle(person: Person): Boolean {
//
//        //Check angle of left shoulder and right shoulder is 180 degree
//        val leftShoulder = person.keyPoints[BodyPart.LEFT_SHOULDER.position]
//        val rightShoulder = person.keyPoints[BodyPart.RIGHT_SHOULDER.position]
//
//        //if left shoulder or right shoulder is not detected, return false
//        if (leftShoulder.score <0.5 || rightShoulder.score < 0.5) {
//            return false
//        }
//
//        val leftShoulderCoordinate = leftShoulder.coordinate
//        val rightShoulderCoordinate = rightShoulder.coordinate
//
//        //use atan2 to calculate angle
//        val angle = atan2((leftShoulderCoordinate.y - rightShoulderCoordinate.y).toDouble(), (leftShoulderCoordinate.x - rightShoulderCoordinate.x).toDouble())
//        val angleDegree = Math.toDegrees(angle)
//        Log.d("AngleCheckingUtils", "Angle is $angleDegree degree")
//        //if angle is 180 degree, create a toast to notify user
//        if (angleDegree > -10 && angleDegree < 10) {
//            return true
//        }
//        return false
//    }

    fun check3PointAngle(BodyPart1: KeyPoint, BodyPart2: KeyPoint, BodyPart3: KeyPoint, minAngle: Float, maxAngle: Float): Boolean {
        val BodyPart1Coordinate = BodyPart1.coordinate
        val BodyPart2Coordinate = BodyPart2.coordinate
        val BodyPart3Coordinate = BodyPart3.coordinate

        val angle = atan2((BodyPart1Coordinate.y - BodyPart2Coordinate.y).toDouble(), (BodyPart1Coordinate.x - BodyPart2Coordinate.x).toDouble()) -
                atan2((BodyPart3Coordinate.y - BodyPart2Coordinate.y).toDouble(), (BodyPart3Coordinate.x - BodyPart2Coordinate.x).toDouble())
        var angleDegree = Math.toDegrees(angle)
        //Make it positive and less than 180 degree
        if (angleDegree < 0) {
            angleDegree += 360
        }
        if (angleDegree > 180) {
            angleDegree -= 180
        }


        Log.d("AngleCheckingUtils", "Angle is $angleDegree degree")
        if (angleDegree > minAngle &&  angleDegree < maxAngle) {
            return true
        }
        return false
    }
}