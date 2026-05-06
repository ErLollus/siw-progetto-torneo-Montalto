package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.Utente;
import it.uniroma3.siw.torneo.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UtenteController {

    @Autowired
    private UtenteService utenteService;

    // Mostra il form di registrazione
    @GetMapping("/register")
    public String formRegisterUser(Model model) {
        model.addAttribute("utente", new Utente());
        return "formRegisterUser.html";
    }

    // Salva l'utente appena registrato
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("utente") Utente utente) {
        // Impostiamo un ruolo predefinito di base
        utente.setRuolo("USER");
        this.utenteService.save(utente);
        return "redirect:/"; // Torna alla home dopo la registrazione
    }
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // <-- IMPORTANTE: Solo "login", senza ".html"
    }
}