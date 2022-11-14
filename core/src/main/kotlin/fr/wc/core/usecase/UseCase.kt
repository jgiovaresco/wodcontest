package fr.wc.core.usecase

import arrow.core.Either
import fr.wc.core.error.ApplicationError
import fr.wc.core.model.command.Command

interface UseCase<C : Command, R> {
    suspend fun execute(command: C): Either<ApplicationError, R>
}
