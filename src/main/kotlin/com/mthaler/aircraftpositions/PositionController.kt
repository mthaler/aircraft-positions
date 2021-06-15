package com.mthaler.aircraftpositions

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux


@Controller
class PositionController(private val repository: AircraftRepository) {

    private val client = WebClient.create("http://localhost:7634/aircraft")

    @GetMapping("/aircraft")
    fun getCurrentAircraftPositions(model: Model): String {
        val aircraftFlux = repository.deleteAll()
            .thenMany(client.get()
                .retrieve()
                .bodyToFlux<Aircraft>()
                .filter { !it.reg.isNullOrEmpty() }
                .flatMap { repository.save(it) })

        model.addAttribute("currentPositions", aircraftFlux)
        return "positions"
    }
}
