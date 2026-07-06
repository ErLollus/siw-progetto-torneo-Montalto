package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.Partita;
import it.uniroma3.siw.torneo.service.ArbitroService;
import it.uniroma3.siw.torneo.service.PartitaService;
import it.uniroma3.siw.torneo.service.SquadraService;
import it.uniroma3.siw.torneo.service.TorneoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PartitaController {

    @Autowired
    private PartitaService partitaService;
    @Autowired
    private TorneoService torneoService;
    @Autowired
    private SquadraService squadraService;
    @Autowired
    private ArbitroService arbitroService;

    @GetMapping("/partite")
    public String getPartite(Model model) {
        // Usiamo la variante con join fetch per evitare le N+1 query (Sez. 8 PDF)
        model.addAttribute("partite", this.partitaService.findAllConDettagli());
        model.addAttribute("squadre", this.squadraService.findAll());
        return "partite/list.html";
    }

    private void popolaSelezioni(Model model) {
        model.addAttribute("tornei", this.torneoService.findAll());
        model.addAttribute("squadre", this.squadraService.findAll());
        model.addAttribute("arbitri", this.arbitroService.findAll());
    }

    @GetMapping("/admin/formNewPartita")
    public String formNewPartita(Model model) {
        model.addAttribute("partita", new Partita());
        popolaSelezioni(model);
        return "admin/partite/formNew.html";
    }

    @PostMapping("/admin/partita")
    public String newPartita(@Valid @ModelAttribute("partita") Partita partita,
                             BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            popolaSelezioni(model);
            return "admin/partite/formNew.html";
        }
        // Se sono stati inseriti entrambi i risultati, la partita è TERMINATA
        if (partita.getGoalsHome() != null && partita.getGoalsAway() != null) {
            partita.setStato("TERMINATA");
        } else if (partita.getStato() == null || partita.getStato().isBlank()) {
            partita.setStato("PROGRAMMATA");
        }
        this.partitaService.save(partita);
        return "redirect:/partite";
    }

    @GetMapping("/admin/formUpdatePartita/{id}")
    public String formUpdatePartita(@PathVariable("id") Long id, Model model) {
        model.addAttribute("partita", this.partitaService.findById(id));
        popolaSelezioni(model);
        return "admin/partite/formUpdate.html";
    }

    @PostMapping("/admin/updatePartita")
    public String updatePartita(@Valid @ModelAttribute("partita") Partita partita,
                                BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            popolaSelezioni(model);
            return "admin/partite/formUpdate.html";
        }
        // Se sono presenti i gol, registriamo il risultato -> stato TERMINATA
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
