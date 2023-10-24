package fr.wc.configuration

import fr.wc.inmemory.repository.InMemoryChampionshipRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RepositoryConfiguration {
    @Bean
    fun championshipRepository() = InMemoryChampionshipRepository()
}
