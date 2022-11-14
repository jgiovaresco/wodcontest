package fr.wc.core.model

data class Division(val gender: Gender, val level: Level) {
    fun accept(athlete: Athlete): Boolean {
        return gender == athlete.gender
    }
}
