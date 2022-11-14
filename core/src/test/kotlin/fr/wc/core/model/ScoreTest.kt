package fr.wc.core.model

import io.kotest.core.spec.style.FunSpec
import strikt.api.*
import strikt.assertions.*

class ScoreTest :
  FunSpec({
    context("TimeScore") {
      test("same time is equal") {
        val time1 = TimeScore(10u)
        val time2 = TimeScore(10u)

        expectThat(time1).isEqualTo(time2)
      }

      test("sort a list of score from fastest time to the slowest") {
        val scores = listOf(TimeScore(100u), TimeScore(95u), TimeScore(105u), TimeScore(90u))

        expectThat(scores.sorted().map { it.value }).isEqualTo(listOf(90u, 95u, 100u, 105u))
      }
    }

    context("WeightScore") {
      test("same weight is equal") {
        val weight1 = WeightScore(120u)
        val weight2 = WeightScore(120u)

        expectThat(weight1).isEqualTo(weight2)
      }

      test("sort a list of score from heaviest weight to the lightest") {
        val scores = listOf(WeightScore(100u), WeightScore(95u), WeightScore(105u), WeightScore(90u))

        expectThat(scores.sorted().map { it.value }).isEqualTo(listOf(105u, 100u, 95u, 90u))
      }
    }

    context("RepScore") {
      test("same reps is equal") {
        val reps1 = RepScore(120u)
        val reps2 = RepScore(120u)

        expectThat(reps1).isEqualTo(reps2)
      }

      test("sort a list of score from highest reps to the lowest") {
        val scores = listOf(WeightScore(100u), WeightScore(95u), WeightScore(105u), WeightScore(90u))

        expectThat(scores.sorted().map { it.value }).isEqualTo(listOf(105u, 100u, 95u, 90u))
      }
    }
  })
