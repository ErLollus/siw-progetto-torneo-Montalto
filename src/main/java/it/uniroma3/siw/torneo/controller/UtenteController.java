package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.Ruolo;
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

    // pagina con il form per registrarsi
    @GetMapping("/register")
    public String formRegisterUser(Model model) {
        model.addAttribute("utente", new Utente());
        return "auth/register.html";
    }

    // qui arrivano i dati del form e salvo il nuovo utente
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("utente") Utente utente,
                               BindingResult bindingResult) {
        // controllo se lo username è già preso da qualcun altro
        if (utente.getUsername() != null && this.utenteService.existsByUsername(utente.getUsername())) {
            bindingResult.rejectValue("username", "utente.username.esistente",
                    "Username già in uso, scegline un altro.");
        }
        if (bindingResult.hasErrors()) {
            return "auth/register.html";
        }
        // chi si registra da solo parte sempre come USER, l'ADMIN lo creo io a mano in InitData
        utente.setRuolo(Ruolo.USER);
        this.utenteService.save(utente);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "auth/login"; // occhio: NON devo mettere ".html" altrimenti Thymeleaf non trova il template
    }
}
