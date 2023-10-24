package fr.wc.rest.exception

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestControllerAdvice
class GeneralExceptionHandler : ResponseEntityExceptionHandler() {
    data class ErrorDetail(val code: String?, val message: String?)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(ex: MethodArgumentNotValidException): ResponseEntity<Any> {
        val detail = ex.body
        detail.setProperty("errors",
                ex.bindingResult.fieldErrors
                        .map { Pair(it.field, ErrorDetail(it.code, it.defaultMessage)) }
                        .groupBy(
                                keySelector = { it.first },
                                valueTransform = { it.second }
                        )
        )

        return ResponseEntity(detail, ex.headers, ex.statusCode)
    }

    override fun handleWebExchangeBindException(ex: WebExchangeBindException, headers: HttpHeaders, status: HttpStatusCode, exchange: ServerWebExchange): Mono<ResponseEntity<Any>> {
        val detail = ex.body
        detail.setProperty("errors",
                ex.bindingResult.fieldErrors
                        .map { Pair(it.field, ErrorDetail(it.code, it.defaultMessage)) }
                        .groupBy(
                                keySelector = { it.first },
                                valueTransform = { it.second }
                        )
        )

        return super.handleWebExchangeBindException(ex, headers, status, exchange)
    }

}
