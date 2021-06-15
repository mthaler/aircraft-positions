package com.mthaler.aircraftpositions

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PositionController2(private val retriever: PositionRetriever) {
    @GetMapping("/aircraft2")
    fun getCurrentAircraftPositions() = retriever.retrieveAircraftPositions()
}