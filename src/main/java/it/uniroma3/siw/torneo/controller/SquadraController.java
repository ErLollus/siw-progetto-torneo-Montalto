package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.Squadra;
import it.uniroma3.siw.torneo.service.FileStorageService;
import it.uniroma3.siw.torneo.service.SquadraService;
import it.uniroma3.siw.torneo.service.TorneoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SquadraController {

    @Autowired
    private SquadraService squadraService;

    @Autowired
    private TorneoService torneoService;

    @Autowired
    private FileStorageService fileStorageService;

    // ELENCO GENERALE SQUADRE
    @GetMapping("/squadre")
    public String getSquadre(@RequestParam(defaultValue = "") String q,Model model) {
        model.addAttribute("squadre",this.squadraService.count());
        model.addAttribute("squadre",this.squadraService.orderByNomeAsc());
        model.addAttribute("squadre", this.squadraService.cerca(q));
        model.addAttribute("q",q);
        return "squadre/list.html";
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
            return "squadre/show.html";
        }
        return "redirect:/squadre";
    }

    // --- ADMIN: CREAZIONE ---
    @GetMapping("/admin/formNewSquadra")
    public String formNewSquadra(Model model) {
        model.addAttribute("squadra", new Squadra());
        model.addAttribute("tornei", this.torneoService.findAll());
        return "admin/squadre/formNew.html";
    }

    @PostMapping("/admin/squadra")
    public String newSquadra(@Valid @ModelAttribute("squadra") Squadra squadra,
                             BindingResult bindingResult,
                             @RequestParam(value = "immagineFile", required = false) MultipartFile immagineFile,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tornei", this.torneoService.findAll());
            return "admin/squadre/formNew.html";
        }
        if (immagineFile != null && !immagineFile.isEmpty()) {
            squadra.setImmagine(this.fileStorageService.salva(immagineFile));
        }
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
        model.addAttribute("tornei", this.torneoService.findAll());
        return "admin/squadre/formUpdate";
    }

    @PostMapping("/admin/updateSquadra")
    public String updateSquadra(@Valid @ModelAttribute("squadra") Squadra squadra,
                                BindingResult bindingResult,
                                @RequestParam(value = "immagineFile", required = false) MultipartFile immagineFile,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("tornei", this.torneoService.findAll());
            return "admin/squadre/formUpdate";
        }
        if (immagineFile != null && !immagineFile.isEmpty()) {
            squadra.setImmagine(this.fileStorageService.salva(immagineFile));
        }
        this.squadraService.save(squadra);
        return "redirect:/squadre";
    }
}