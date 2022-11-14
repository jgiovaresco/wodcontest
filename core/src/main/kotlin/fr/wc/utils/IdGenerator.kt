package fr.wc.utils

import java.util.*

class IdGenerator {
    companion object Uuid {
        var generateFn: () -> String = generateUUID()

        fun generate(): String {
            return generateFn()
        }

        fun overrideGenerateFn(fn: () -> String) {
            generateFn = fn
        }

        fun reset() {
            generateFn = generateUUID()
        }
    }
}

fun generateUUID(): () -> String = { UUID.randomUUID().toString() }
