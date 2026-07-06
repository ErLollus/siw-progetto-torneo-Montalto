package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.Utente;
import it.uniroma3.siw.torneo.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
        return "auth/register.html";
    }

    // Salva l'utente appena registrato
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("utente") Utente utente,
                               BindingResult bindingResult) {
        // Username già in uso?
        if (utente.getUsername() != null && this.utenteService.existsByUsername(utente.getUsername())) {
            bindingResult.rejectValue("username", "utente.username.esistente",
                    "Username già in uso, scegline un altro.");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register.html";
        }
        // Ruolo predefinito per i nuovi iscritti
        utente.setRuolo("USER");
        this.utenteService.save(utente);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login"; // <-- IMPORTANTE: Solo "login", senza ".html"
    }
}
