package it.uniroma3.siw.torneo.controller;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Gestione centralizzata degli errori (criterio "gestione degli errori" della
 * Sezione 11 del PDF). Mostra una pagina di errore leggibile invece dello
 * stack trace di default.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(Model model) {
        model.addAttribute("titolo", "Accesso negato");
        model.addAttribute("messaggio",
                "Non hai i permessi necessari per accedere a questa pagina.");
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericError(Exception ex, Model model) {
        model.addAttribute("titolo", "Si è verificato un errore");
        model.addAttribute("messaggio",
                "Qualcosa è andato storto durante l'elaborazione della richiesta.");
        model.addAttribute("dettaglio", ex.getMessage());
        return "error/error";
    }
}
