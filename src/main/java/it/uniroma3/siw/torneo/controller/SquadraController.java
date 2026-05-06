package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.Squadra;
import it.uniroma3.siw.torneo.model.Torneo;
import it.uniroma3.siw.torneo.service.SquadraService;
import it.uniroma3.siw.torneo.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class SquadraController {

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private TorneoService torneoService;

    // ELENCO GENERALE SQUADRE
    @GetMapping("/squadre")
    public String getSquadre(Model model) {
        model.addAttribute("squadre", this.squadraService.findAll());
        return "squadre.html";
    }

    // --- AGGIUNTA: DETTAGLIO SQUADRA (LA ROSA) ---
    // Questa è la parte richiesta per visualizzare i giocatori di una squadra
    @GetMapping("/squadra/{id}")
    public String getSquadra(@PathVariable("id") Long id, Model model) {
        Squadra squadra = this.squadraService.findById(id);
        if (squadra != null) {
            model.addAttribute("squadra", squadra);
            // Passiamo i giocatori legati a questa squadra
            model.addAttribute("giocatori", squadra.getGiocatori());
            return "squadra.html";
        }
        return "redirect:/squadre";
    }

    // --- ADMIN: CREAZIONE ---
    @GetMapping("/admin/formNewSquadra")
    public String formNewSquadra(Model model) {
        model.addAttribute("squadra", new Squadra());
        model.addAttribute("tornei", this.torneoService.findAll());
        return "admin/formNewSquadra.html";
    }

    @PostMapping("/admin/squadra")
    public String newSquadra(@ModelAttribute("squadra") Squadra squadra) {
        this.squadraService.save(squadra);
        return "redirect:/squadre";
    }

    // --- ADMIN: ELIMINAZIONE ---
    @GetMapping("/admin/deleteSquadra/{id}")
    public String deleteSquadra(@PathVariable("id") Long id) {
        this.squadraService.deleteById(id);
        return "redirect:/squadre";
    }

    // --- ADMIN: MODIFICA ---
    @GetMapping("/admin/formUpdateSquadra/{id}")
    public String formUpdateSquadra(@PathVariable("id") Long id, Model model) {
        Squadra squadra = this.squadraService.findById(id);
        model.addAttribute("squadra", squadra);
        model.addAttribute("tuttiTornei", this.torneoService.findAll());
        return "admin/formUpdateSquadra";
    }

    @PostMapping("/admin/updateSquadra")
    public String updateSquadra(@ModelAttribute("squadra") Squadra squadra) {
        this.squadraService.save(squadra);
        return "redirect:/squadre";
    }
}