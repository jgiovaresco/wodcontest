package fr.wc.core.usecase

import arrow.core.Either
import fr.wc.core.error.ApplicationError

interface UseCase<C, R> {
    suspend fun execute(input: C): Either<ApplicationError, R>
}
