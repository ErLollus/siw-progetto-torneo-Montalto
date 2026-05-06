package it.uniroma3.siw.torneo;

import it.uniroma3.siw.torneo.model.Giocatore;
import it.uniroma3.siw.torneo.model.Squadra;
import it.uniroma3.siw.torneo.model.Torneo;
import it.uniroma3.siw.torneo.service.GiocatoreService;
import it.uniroma3.siw.torneo.service.SquadraService;
import it.uniroma3.siw.torneo.service.TorneoService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;

@Component
public class InitData implements CommandLineRunner {

    private final TorneoService torneoService;
    private final SquadraService squadraService;
    private final GiocatoreService giocatoreService;

    public InitData(TorneoService torneoService, SquadraService squadraService, GiocatoreService giocatoreService) {
        this.torneoService = torneoService;
        this.squadraService = squadraService;
        this.giocatoreService = giocatoreService;
    }

    @Override
    public void run(String... args) throws Exception {

        // CONTROLLO: Se ci sono già tornei, non fare nulla
        if (torneoService.findAll().isEmpty()) {
            System.out.println("--- DB Vuoto: Inserimento dati iniziali ---");

            Torneo champions = new Torneo();
            champions.setNome("Champions League Roma 3");
            champions.setAnno(2026);
            champions.setDescrizione("Torneo Universitario");
            torneoService.saveTorneo(champions);

            Squadra asRoma3 = new Squadra();
            asRoma3.setNome("A.S. Roma 3");
            asRoma3.setCitta("Roma");
            asRoma3.setAnnoFondazione(2020);
            asRoma3.setTornei(new ArrayList<>());
            asRoma3.getTornei().add(champions);
            squadraService.save(asRoma3);

            Giocatore g1 = new Giocatore();
            g1.setNome("Francesco");
            g1.setCognome("Totti");
            g1.setRuolo("Attaccante");
            g1.setDataNascita(LocalDate.of(1976, 9, 27));
            g1.setAltezza(180);
            g1.setSquadra(asRoma3);
            giocatoreService.save(g1);

            System.out.println("--- Popolamento completato ---");
        } else {
            System.out.println("--- DB già popolato: Salto l'inserimento automatico ---");
        }
    }
}