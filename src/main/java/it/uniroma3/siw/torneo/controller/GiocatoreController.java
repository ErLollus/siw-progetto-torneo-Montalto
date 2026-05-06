package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.Giocatore;
import it.uniroma3.siw.torneo.service.GiocatoreService;
import it.uniroma3.siw.torneo.service.SquadraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class GiocatoreController {

    @Autowired
    private GiocatoreService giocatoreService;

    @GetMapping("/giocatori")
    public String getGiocatori(Model model) {
        model.addAttribute("giocatori", this.giocatoreService.findAll());
        return "giocatori.html";
    }
    @Autowired
    private SquadraService squadraService;

    @GetMapping("/admin/formNewGiocatore")
    public String formNewGiocatore(Model model) {
        model.addAttribute("giocatore", new Giocatore());
        model.addAttribute("squadre", this.squadraService.findAll());
        return "admin/formNewGiocatore.html";
    }

    @PostMapping("/admin/giocatore")
    public String newGiocatore(@ModelAttribute("giocatore") Giocatore giocatore) {
        this.giocatoreService.save(giocatore);
        return "redirect:/giocatori";
    }
    @GetMapping("/admin/deleteGiocatore/{id}")
    public String deleteGiocatore(@PathVariable("id") Long id) {
        this.giocatoreService.deleteById(id);
        return "redirect:/giocatori";
    }
    @GetMapping("/admin/formUpdateGiocatore/{id}")
    public String formUpdateGiocatore(@PathVariable("id") Long id, Model model) {
        model.addAttribute("giocatore", this.giocatoreService.findById(id));
        model.addAttribute("squadre", this.squadraService.findAll()); // Per cambiare squadra
        return "admin/formUpdateGiocatore.html";
    }
    @PostMapping("/admin/updateGiocatore")
    public String updateGiocatore(@ModelAttribute("giocatore") Giocatore giocatore) {
        this.giocatoreService.save(giocatore);
        return "redirect:/giocatori";
    }
}