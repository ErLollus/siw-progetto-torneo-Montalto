package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.SquadraClassifica;
import it.uniroma3.siw.torneo.model.Torneo;
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

import java.util.List;

@Controller
public class TorneoController {

    @Autowired
    private TorneoService torneoService;

    @GetMapping("/tornei") // pagina con l'elenco di tutti i tornei
    public String getTornei(Model model) {
        // prendo tutti i tornei dal service e li metto nel Model per passarli alla pagina HTML
        model.addAttribute("tornei",this.torneoService.count());
        model.addAttribute("tornei", this.torneoService.findAll());
        return "tornei/list.html"; // Vai a cercare il file tornei.html nella cartella templates
    }

    // dettaglio di un torneo: squadre iscritte, calendario e link alla classifica (sezione 4.1 del PDF)
    @GetMapping("/torneo/{id}")
    public String getTorneo(@PathVariable("id") Long id, Model model) {
        Torneo torneo = this.torneoService.findById(id);
        if (torneo == null) {
            return "redirect:/tornei";
        }
        model.addAttribute("torneo", torneo);
        return "tornei/show";
    }

    // form per creare un torneo nuovo
    @GetMapping("/admin/formNewTorneo")
    public String formNewTorneo(Model model) {
        model.addAttribute("torneo", new Torneo()); // oggetto vuoto che il form andrà a riempire
        return "admin/tornei/formNew.html";
    }

    // riceve i dati del form e salva il torneo
    @PostMapping("/admin/torneo")
    public String newTorneo(@Valid @ModelAttribute("torneo") Torneo torneo,
                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/tornei/formNew.html";
        }
        this.torneoService.saveTorneo(torneo); // salvo il torneo nuovo
        return "redirect:/tornei"; // dopo il salvataggio torno alla lista
    }
    @GetMapping("/admin/deleteTorneo/{id}")
    public String deleteTorneo(@PathVariable("id") Long id) {
        this.torneoService.deleteById(id);
        return "redirect:/tornei";
    }

    // form di modifica, già riempito con i dati del torneo esistente
    @GetMapping("/admin/formUpdateTorneo/{id}")
    public String formUpdateTorneo(@PathVariable("id") Long id, Model model) {
        // cerco il torneo sul database tramite l'id
        Torneo torneo = this.torneoService.findById(id);
        model.addAttribute("torneo", torneo);
        return "admin/tornei/formUpdate.html";
    }

    // salva le modifiche (uso lo stesso metodo del service che uso per la creazione)
    @PostMapping("/admin/updateTorneo")
    public String updateTorneo(@Valid @ModelAttribute("torneo") Torneo torneo,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/tornei/formUpdate.html";
        }
        // se l'oggetto torneo ha già l'id, save() fa l'update invece dell'insert
        this.torneoService.saveTorneo(torneo);
        return "redirect:/tornei";
    }
    @GetMapping("/torneo/{id}/classifica")
    public String visualizzaClassifica(@PathVariable("id") Long id, Model model) {
        Torneo torneo = this.torneoService.findById(id);
        List<SquadraClassifica> classifica = this.torneoService.calcolaClassifica(id);

        model.addAttribute("torneo", torneo);
        model.addAttribute("classifica", classifica);
        return "tornei/classifica.html";
    }
    @GetMapping("/torneo/{id}/classifica-react")
    public String visualizzaClassificaReact(@PathVariable("id") Long id, Model model) {
        // passo solo l'id, poi ci pensa React a chiamare l'API e caricare i dati
        model.addAttribute("torneoId", id);
        return "tornei/classifica-react";
    }
}