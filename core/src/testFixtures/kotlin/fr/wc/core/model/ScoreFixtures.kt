package fr.wc.core.model

import kotlin.random.Random
import kotlin.random.nextUInt

fun aTimeScore(valueInSeconds: UInt = Random.nextUInt(60u..1500u)) = TimeScore(valueInSeconds)
fun aWeightScore(valueInKg: UInt = Random.nextUInt(20u..200u)) = WeightScore(valueInKg)
fun aRepScore(repNumber: UInt = Random.nextUInt(0u..500u)) = RepScore(repNumber)
