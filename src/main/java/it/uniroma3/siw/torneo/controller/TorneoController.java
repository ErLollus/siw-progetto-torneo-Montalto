package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.SquadraClassifica;
import it.uniroma3.siw.torneo.model.Torneo;
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
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    @GetMapping("/tornei") // Quando l'utente va su http://localhost:8080/tornei
    public String getTornei(Model model) {
        // Prendi tutti i tornei dal Service e mettili nel "Model" (il cestino per la pagina HTML)
        model.addAttribute("tornei", this.torneoService.findAll());
        return "tornei.html"; // Vai a cercare il file tornei.html nella cartella templates
    }
    // 1. Metodo per mostrare il form di inserimento
    @GetMapping("/admin/formNewTorneo")
    public String formNewTorneo(Model model) {
        model.addAttribute("torneo", new Torneo()); // Passiamo un oggetto vuoto da riempire
        return "admin/formNewTorneo.html";
    }

    // 2. Metodo per ricevere i dati e salvare
    @PostMapping("/admin/torneo")
    public String newTorneo(@ModelAttribute("torneo") Torneo torneo) {
        this.torneoService.saveTorneo(torneo); // Salviamo il nuovo torneo
        return "redirect:/tornei"; // Dopo il salvataggio, torna alla lista
    }
    @GetMapping("/admin/deleteTorneo/{id}")
    public String deleteTorneo(@PathVariable("id") Long id) {
        this.torneoService.deleteById(id);
        return "redirect:/tornei";
    }

    // 1. Mostra il form di modifica pre-popolato con i dati del torneo
    @GetMapping("/admin/formUpdateTorneo/{id}")
    public String formUpdateTorneo(@PathVariable("id") Long id, Model model) {
        // Cerchiamo il torneo sul DB tramite l'id
        Torneo torneo = this.torneoService.findById(id);
        model.addAttribute("torneo", torneo);
        return "admin/formUpdateTorneo.html";
    }

    // 2. Salva le modifiche (usa lo stesso endpoint del "newTorneo")
    @PostMapping("/admin/updateTorneo")
    public String updateTorneo(@ModelAttribute("torneo") Torneo torneo) {
        // Se 'torneo' ha l'id valorizzato, il save farà l'UPDATE sul DB
        this.torneoService.saveTorneo(torneo);
        return "redirect:/tornei";
    }
    @GetMapping("/torneo/{id}/classifica")
    public String visualizzaClassifica(@PathVariable("id") Long id, Model model) {
        Torneo torneo = this.torneoService.findById(id);
        List<SquadraClassifica> classifica = this.torneoService.calcolaClassifica(id);

        model.addAttribute("torneo", torneo);
        model.addAttribute("classifica", classifica);
        return "classifica.html";
    }
    @GetMapping("/torneo/{id}/classifica-react")
    public String visualizzaClassificaReact(@PathVariable("id") Long id, Model model) {
        // Passiamo solo l'ID, i dati li caricherà React via API
        model.addAttribute("torneoId", id);
        return "classifica_react";
    }
}