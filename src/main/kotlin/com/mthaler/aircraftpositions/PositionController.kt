package com.mthaler.aircraftpositions

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PositionController(private val retriever: PositionRetriever) {

    @GetMapping("/aircraft")
    fun getCurrentAircraftPositions(model: Model): String {
        model.addAttribute("currentPositions", retriever.retrieveAircraftPositions())
        return "positions"
    }
}
