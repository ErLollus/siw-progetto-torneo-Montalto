package it.uniroma3.siw.torneo.controller.rest;

import it.uniroma3.siw.torneo.model.SquadraClassifica;
import it.uniroma3.siw.torneo.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // qui non ritorno una view ma JSON puro, così lo può leggere React
@RequestMapping("/api")
public class ClassificaRestController {

    @Autowired
    private TorneoService torneoService;

    @GetMapping("/classifica/{id}")
    public List<SquadraClassifica> getClassificaData(@PathVariable("id") Long id) {
        // riuso lo stesso metodo del service che uso già per la classifica in Thymeleaf
        return this.torneoService.calcolaClassifica(id);
    }
}