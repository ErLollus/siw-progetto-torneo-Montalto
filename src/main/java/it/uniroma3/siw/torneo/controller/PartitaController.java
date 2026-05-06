package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.Partita;
import it.uniroma3.siw.torneo.service.ArbitroService;
import it.uniroma3.siw.torneo.service.PartitaService;
import it.uniroma3.siw.torneo.service.SquadraService;
import it.uniroma3.siw.torneo.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PartitaController {

    @Autowired
    private PartitaService partitaService;

    @GetMapping("/partite")
    public String getPartite(Model model) {
        model.addAttribute("partite", this.partitaService.findAll());
        return "partite.html";
    }
    @Autowired
    private TorneoService torneoService;
    @Autowired
    private SquadraService squadraService;
    @Autowired
    private ArbitroService arbitroService;

    @GetMapping("/admin/formNewPartita")
    public String formNewPartita(Model model) {
        model.addAttribute("partita", new Partita());
        model.addAttribute("tornei", this.torneoService.findAll());
        model.addAttribute("squadre", this.squadraService.findAll());
        model.addAttribute("arbitri", this.arbitroService.findAll());
        return "admin/formNewPartita.html";
    }

    @PostMapping("/admin/partita")
    public String newPartita(@ModelAttribute("partita") Partita partita) {
        this.partitaService.save(partita);
        return "redirect:/partite";
    }
    @GetMapping("/admin/formUpdatePartita/{id}")
    public String formUpdatePartita(@PathVariable("id") Long id, Model model) {
        model.addAttribute("partita", this.partitaService.findById(id));
        model.addAttribute("squadre", this.squadraService.findAll());
        model.addAttribute("arbitri", this.arbitroService.findAll());
        model.addAttribute("tornei", this.torneoService.findAll());
        return "admin/formUpdatePartita.html";
    }

    @PostMapping("/admin/updatePartita")
    public String updatePartita(@ModelAttribute("partita") Partita partita) {
        // IMPORTANTE: Assicurati che lo stato sia scritto esattamente in MAIUSCOLO
        // come nel controllo che facciamo nel Service
        if (partita.getGoalsHome() != null && partita.getGoalsAway() != null) {
            partita.setStato("TERMINATA");
        }

        this.partitaService.save(partita);
        return "redirect:/partite";
    }
    @GetMapping("/admin/deletePartita/{id}")
    public String deletePartita(@PathVariable("id") Long id) {
        this.partitaService.deleteById(id);
        return "redirect:/partite";
    }
}