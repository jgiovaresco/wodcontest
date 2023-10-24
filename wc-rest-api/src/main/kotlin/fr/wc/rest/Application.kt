package fr.wc.rest

import fr.wc.configuration.CoreConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.hateoas.config.EnableHypermediaSupport

@SpringBootApplication
@EnableHypermediaSupport(type = [
    EnableHypermediaSupport.HypermediaType.HAL,
    EnableHypermediaSupport.HypermediaType.HAL_FORMS,
])
@ComponentScan(basePackageClasses = [CoreConfiguration::class])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
