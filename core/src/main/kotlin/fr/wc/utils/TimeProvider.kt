package fr.wc.utils

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime

class TimeProvider {
    companion object Time {
        var clock: Clock = Clock.systemDefaultZone()

        fun now(): LocalDateTime {
            return LocalDateTime.now(clock)
        }

        fun today(): LocalDate {
            return LocalDate.now(clock)
        }

        fun instantNow(): Instant {
            return Instant.now(clock)
        }

        fun overrideClock(clock: Clock) {
            Time.clock = clock
        }
    }
}
