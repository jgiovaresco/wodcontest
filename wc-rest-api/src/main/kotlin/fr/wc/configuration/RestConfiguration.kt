package fr.wc.configuration

import fr.wc.rest.controller.ChampionshipController
import fr.wc.rest.exception.GeneralExceptionHandler
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackageClasses = [ChampionshipController::class, GeneralExceptionHandler::class])
class RestConfiguration
