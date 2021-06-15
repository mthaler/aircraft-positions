package com.mthaler.aircraftpositions

import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.socket.TextMessage
import java.io.IOException

@EnableScheduling
@Component
class PlaneFinderPoller(private val repository: AircraftRepository, private val handler: WebSocketHandler) {

    private val client = WebClient.create("http://localhost:7634/aircraft")

    @Scheduled(fixedRate = 1000)
    private fun pollPlanes() {
        repository.deleteAll()

        client.get()
            .retrieve()
            .bodyToFlux<Aircraft>()
            .filter { !it.reg.isNullOrEmpty() }
            .toStream()
            .forEach { repository.save(it) }

        repository.findAll().forEach { println(it) }

        sendPositions()
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