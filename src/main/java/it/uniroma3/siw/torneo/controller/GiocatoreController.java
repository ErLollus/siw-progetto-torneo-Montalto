package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.model.Giocatore;
import it.uniroma3.siw.torneo.service.FileStorageService;
import it.uniroma3.siw.torneo.service.GiocatoreService;
import it.uniroma3.siw.torneo.service.SquadraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
@Controller
public class GiocatoreController {

    @Autowired
    private GiocatoreService giocatoreService;
    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/giocatori")
    public String getGiocatori(@RequestParam(defaultValue = "") String q,
                                @RequestParam(defaultValue = "") String cognome,
                                @RequestParam(defaultValue = "false") boolean ordina,
                                Model model) {
        if(cognome != null && !cognome.isEmpty()) {
            model.addAttribute("giocatori", this.giocatoreService.findByCognomeOrderedByCognomeAsc(cognome));
            return "giocatori/list.html";
        }
        Pageable pageable = ordina
                ? PageRequest.of(0, 10, Sort.by("cognome").ascending())
                : PageRequest.of(0, 10);
        Page<Giocatore> pagina = this.giocatoreService.cerca(q, pageable);
        model.addAttribute("totalegiocatori", this.giocatoreService.count());
        model.addAttribute("giocatori", pagina.getContent());
        model.addAttribute("q", q);
        model.addAttribute("ordina", ordina);
        return "giocatori/list.html";
    }
    @Autowired
    private SquadraService squadraService;

    @GetMapping("/admin/formNewGiocatore")
    public String formNewGiocatore(Model model) {
        model.addAttribute("giocatore", new Giocatore());
        model.addAttribute("squadre", this.squadraService.findAll());
        return "admin/giocatori/formNew.html";
    }

    @PostMapping("/admin/giocatore")
    public String newGiocatore(@Valid @ModelAttribute("giocatore") Giocatore giocatore,
                               BindingResult bindingResult,
                               @RequestParam(value = "immagineFile", required = false) MultipartFile immagineFile,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("squadre", this.squadraService.findAll());
            return "admin/giocatori/formNew.html";
        }
        if (immagineFile != null && !immagineFile.isEmpty()) {
            giocatore.setImmagine(this.fileStorageService.salva(immagineFile));
        }
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
        return "admin/giocatori/formUpdate.html";
    }
    @PostMapping("/admin/updateGiocatore")
    public String updateGiocatore(@Valid @ModelAttribute("giocatore") Giocatore giocatore,
                                  BindingResult bindingResult,
                                  @RequestParam(value = "immagineFile", required = false) MultipartFile immagineFile,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("squadre", this.squadraService.findAll());
            return "admin/giocatori/formUpdate.html";
        }
        if (immagineFile != null && !immagineFile.isEmpty()) {
            giocatore.setImmagine(this.fileStorageService.salva(immagineFile));
        }
        this.giocatoreService.save(giocatore);
        return "redirect:/giocatori";
    }
}