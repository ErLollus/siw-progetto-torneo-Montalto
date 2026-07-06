package it.uniroma3.siw.torneo.controller.rest;

import it.uniroma3.siw.torneo.model.SquadraClassifica;
import it.uniroma3.siw.torneo.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Indica che questo controller restituisce dati (JSON), non viste HTML
@RequestMapping("/api")
public class ClassificaRestController {

    @Autowired
    private TorneoService torneoService;

    @GetMapping("/classifica/{id}")
    public List<SquadraClassifica> getClassificaData(@PathVariable("id") Long id) {
        // Usiamo il metodo che abbiamo già testato e che funziona!
        return this.torneoService.calcolaClassifica(id);
    }
}