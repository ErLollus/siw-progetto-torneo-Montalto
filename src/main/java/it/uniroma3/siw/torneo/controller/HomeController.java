package it.uniroma3.siw.torneo.controller;

import it.uniroma3.siw.torneo.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private TorneoService torneoService;

    @GetMapping({"/", "/index"})
    public String home(Model model) {
        model.addAttribute("tornei", this.torneoService.count());
        return "index";
    }
}
