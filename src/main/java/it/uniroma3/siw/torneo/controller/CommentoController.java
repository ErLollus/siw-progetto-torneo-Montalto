package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.Commento;
import it.uniroma3.siw.torneo.model.Partita;
import it.uniroma3.siw.torneo.model.Utente;
import it.uniroma3.siw.torneo.service.CommentoService;
import it.uniroma3.siw.torneo.service.PartitaService;
import it.uniroma3.siw.torneo.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Controller per le funzionalità degli utenti registrati (sezione 4.2 del
 * PDF): si vede il dettaglio di una partita con i commenti, se ne può
 * aggiungere uno nuovo, e si possono modificare/cancellare SOLO i propri
 * commenti (uno non deve poter toccare i commenti scritti da altri, l'ho
 * controllato bene lato service e non solo nella view).
 */
@Controller
public class CommentoController {

    @Autowired
    private CommentoService commentoService;
    @Autowired
    private PartitaService partitaService;
    @Autowired
    private UtenteService utenteService;

    /**
     * Pagina di dettaglio di una partita, con la lista dei commenti e il
     * form per aggiungerne uno (solo per chi ha fatto login).
     */
    @GetMapping("/partita/{id}")
    public String dettaglioPartita(@PathVariable("id") Long id, Model model, Principal principal) {
        Partita partita = this.partitaService.findById(id);
        if (partita == null) {
            return "redirect:/partite";
        }
        model.addAttribute("partita", partita);
        model.addAttribute("commenti", this.commentoService.findByPartita(partita));
        if (!model.containsAttribute("commento")) {
            model.addAttribute("commento", new Commento());
        }
        model.addAttribute("usernameCorrente", principal != null ? principal.getName() : null);
        return "partite/show";
    }

    /**
     * Riceve il form e salva un nuovo commento sulla partita.
     */
    @PostMapping("/partita/{id}/commento")
    public String aggiungiCommento(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("commento") Commento commento,
                                   BindingResult bindingResult,
                                   Principal principal,
                                   Model model) {
        Partita partita = this.partitaService.findById(id);
        if (partita == null) {
            return "redirect:/partite";
        }
        if (bindingResult.hasErrors()) {
            // se ci sono errori di validazione ricarico la pagina di dettaglio mostrandoli
            model.addAttribute("partita", partita);
            model.addAttribute("commenti", this.commentoService.findByPartita(partita));
            model.addAttribute("usernameCorrente", principal.getName());
            return "partite/show";
        }
        Utente autore = this.utenteService.findByUsername(principal.getName());
        this.commentoService.crea(commento, partita, autore);
        return "redirect:/partita/" + id;
    }

    /**
     * Form per modificare un commento, ma solo se è mio.
     */
    @GetMapping("/commento/{id}/edit")
    public String formModificaCommento(@PathVariable("id") Long id, Model model,
                                       Principal principal, RedirectAttributes ra) {
        Commento commento = this.commentoService.findById(id);
        if (commento == null) {
            return "redirect:/partite";
        }
        if (!this.commentoService.isAutore(id, principal.getName())) {
            ra.addFlashAttribute("erroreCommento", "Puoi modificare solo i tuoi commenti.");
            return "redirect:/partita/" + commento.getPartita().getId();
        }
        model.addAttribute("commento", commento);
        return "commenti/formUpdate";
    }

    /**
     * Salva la modifica di un commento (ricontrollo che sia mio).
     */
    @PostMapping("/commento/{id}/update")
    public String aggiornaCommento(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("commento") Commento commento,
                                   BindingResult bindingResult,
                                   Principal principal,
                                   Model model,
                                   RedirectAttributes ra) {
        Commento esistente = this.commentoService.findById(id);
        if (esistente == null) {
            return "redirect:/partite";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("commento", esistente);
            return "commenti/formUpdate";
        }
        boolean ok = this.commentoService.aggiornaSeAutore(id, commento.getTesto(), principal.getName());
        if (!ok) {
            ra.addFlashAttribute("erroreCommento", "Puoi modificare solo i tuoi commenti.");
        }
        return "redirect:/partita/" + esistente.getPartita().getId();
    }

    /**
     * Cancella un commento: può farlo l'autore, oppure un ADMIN su
     * qualsiasi commento (mi sembrava utile per moderare eventuali
     * commenti fuori luogo).
     */
    @PostMapping("/commento/{id}/delete")
    public String eliminaCommento(@PathVariable("id") Long id, Authentication authentication, RedirectAttributes ra) {
        Commento commento = this.commentoService.findById(id);
        if (commento == null) {
            return "redirect:/partite";
        }
        Long partitaId = commento.getPartita().getId();
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        boolean ok = this.commentoService.elimina(id, authentication.getName(), isAdmin);
        if (!ok) {
            ra.addFlashAttribute("erroreCommento", "Puoi eliminare solo i tuoi commenti.");
        }
        return "redirect:/partita/" + partitaId;
    }
}
