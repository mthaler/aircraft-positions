package com.mthaler.aircraftpositions

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.socket.TextMessage
import java.io.IOException

@Component
@EnableScheduling
class PositionRetriever(private val repository: AircraftRepository, private val handler: WebSocketHandler) {
    private val client = WebClient.create("http://localhost:7634")

    @Scheduled(fixedRate = 1000)
    fun retrieveAircraftPositions(): Iterable<Aircraft> {
        repository.deleteAll()

        client.get()
            .uri("/aircraft")
            .retrieve()
            .bodyToFlux<Aircraft>()
            .filter { !it.reg.isNullOrEmpty() }
            .toStream()
            .forEach { repository.save(it) }

        sendPositions()
        return repository.findAll()
    }

    private fun sendPositions() {
        if (repository.count() > 0) {
            for (sessionInList in handler.getSessionList()) {
                try {
                    sessionInList.sendMessage(
                        TextMessage(repository.findAll().toString())
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}