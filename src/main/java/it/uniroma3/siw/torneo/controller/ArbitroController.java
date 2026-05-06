package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.Arbitro;
import it.uniroma3.siw.torneo.service.ArbitroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ArbitroController {

    @Autowired
    private ArbitroService arbitroService;

    // Mostra la lista di tutti gli arbitri
    @GetMapping("/arbitri")
    public String getArbitri(Model model) {
        model.addAttribute("arbitri", this.arbitroService.findAll());
        return "arbitri.html";
    }

    // Mostra il form per inserire un nuovo arbitro
    @GetMapping("/admin/formNewArbitro")
    public String formNewArbitro(Model model) {
        model.addAttribute("arbitro", new Arbitro());
        return "admin/formNewArbitro.html";
    }

    // Riceve i dati dal form e salva l'arbitro
    @PostMapping("/admin/arbitro")
    public String newArbitro(@ModelAttribute("arbitro") Arbitro arbitro) {
        this.arbitroService.save(arbitro);
        return "redirect:/arbitri";
    }
    @GetMapping("/admin/formUpdateArbitro/{id}")
    public String formUpdateArbitro(@PathVariable("id") Long id, Model model) {
        model.addAttribute("arbitro", this.arbitroService.findById(id));
        return "admin/formUpdateArbitro.html";
    }

    @PostMapping("/admin/updateArbitro")
    public String updateArbitro(@ModelAttribute("arbitro") Arbitro arbitro) {
        this.arbitroService.save(arbitro);
        return "redirect:/arbitri";
    }
    @GetMapping("/admin/deleteArbitro/{id}")
    public String deleteArbitro(@PathVariable("id") Long id) {
        this.arbitroService.deleteById(id);
        return "redirect:/arbitri";
    }
}