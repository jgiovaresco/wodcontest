package fr.wc.utils

import arrow.core.Either

fun <T> last(list: List<T>): T? = Either.catch { list.last() }
    .fold(
        ifLeft = { null },
        ifRight = { it }
    )
