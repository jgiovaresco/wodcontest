package fr.wc.configuration

import fr.wc.core.usecase.CreateChampionship
import fr.wc.core.usecase.UseCase
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.FilterType

@Configuration
@ComponentScan(
        basePackageClasses = [CreateChampionship::class],
        includeFilters = [Filter(type = FilterType.ASSIGNABLE_TYPE, value = [UseCase::class])]
)
class CoreConfiguration
