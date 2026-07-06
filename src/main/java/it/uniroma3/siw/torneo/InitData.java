package it.uniroma3.siw.torneo;

import it.uniroma3.siw.torneo.model.*;
import it.uniroma3.siw.torneo.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Popolamento iniziale del database (eseguito solo se vuoto).
 * Crea utenti (admin + user), tornei, squadre, giocatori, arbitri, partite con
 * risultati e un commento di esempio, così da poter dimostrare tutti i casi
 * d'uso richiesti dal PDF.
 */
@Component
public class InitData implements CommandLineRunner {

    private final TorneoService torneoService;
    private final SquadraService squadraService;
    private final GiocatoreService giocatoreService;
    private final ArbitroService arbitroService;
    private final PartitaService partitaService;
    private final UtenteService utenteService;
    private final CommentoService commentoService;

    public InitData(TorneoService torneoService, SquadraService squadraService,
                    GiocatoreService giocatoreService, ArbitroService arbitroService,
                    PartitaService partitaService, UtenteService utenteService,
                    CommentoService commentoService) {
        this.torneoService = torneoService;
        this.squadraService = squadraService;
        this.giocatoreService = giocatoreService;
        this.arbitroService = arbitroService;
        this.partitaService = partitaService;
        this.utenteService = utenteService;
        this.commentoService = commentoService;
    }

    @Override
    public void run(String... args) {
        if (!torneoService.findAll().isEmpty()) {
            System.out.println("--- DB già popolato: salto l'inserimento automatico ---");
            return;
        }
        System.out.println("--- DB vuoto: inserimento dati iniziali ---");

        // --- UTENTI ---
        if (!utenteService.existsByUsername("admin")) {
            Utente admin = new Utente();
            admin.setNome("Amministratore");
            admin.setCognome("Sistema");
            admin.setUsername("admin");
            admin.setPassword("admin");   // verrà cifrata dal service
            admin.setRuolo("ADMIN");
            utenteService.save(admin);
        }
        if (!utenteService.existsByUsername("user")) {
            Utente user = new Utente();
            user.setNome("Mario");
            user.setCognome("Rossi");
            user.setUsername("user");
            user.setPassword("user");
            user.setRuolo("USER");
            utenteService.save(user);
        }

        // --- TORNEO ---
        Torneo champions = new Torneo();
        champions.setNome("Champions League Roma 3");
        champions.setAnno(2026);
        champions.setDescrizione("Torneo universitario amatoriale di calcio a 11.");
        torneoService.saveTorneo(champions);

        // --- SQUADRE ---
        Squadra roma3 = creaSquadra("A.S. Roma 3", "Roma", 2020, "#8E1F2F", champions);
        Squadra ingegneria = creaSquadra("Ingegneria FC", "Roma", 2018, "#1E5AA8", champions);
        Squadra scienze = creaSquadra("Scienze United", "Roma", 2019, "#2E7D32", champions);
        Squadra lettere = creaSquadra("Lettere City", "Roma", 2021, "#F9A825", champions);

        // --- GIOCATORI ---
        creaGiocatore("Francesco", "Totti", "Attaccante", 180, LocalDate.of(1976, 9, 27), roma3);
        creaGiocatore("Daniele", "De Rossi", "Centrocampista", 184, LocalDate.of(1983, 7, 24), roma3);
        creaGiocatore("Alessandro", "Bianchi", "Portiere", 188, LocalDate.of(1999, 3, 12), ingegneria);
        creaGiocatore("Luca", "Verdi", "Difensore", 182, LocalDate.of(2000, 1, 5), ingegneria);
        creaGiocatore("Marco", "Neri", "Attaccante", 179, LocalDate.of(1998, 11, 30), scienze);
        creaGiocatore("Giovanni", "Gialli", "Centrocampista", 176, LocalDate.of(2001, 6, 18), lettere);

        // --- ARBITRI ---
        Arbitro arbitro1 = creaArbitro("Pierluigi", "Collina", "ARB-001");
        Arbitro arbitro2 = creaArbitro("Nicola", "Rizzoli", "ARB-002");

        // --- PARTITE ---
        Partita p1 = creaPartita(champions, roma3, ingegneria, arbitro1,
                LocalDateTime.now().minusDays(7), "Stadio Roma 3", 3, 1, "TERMINATA");
        creaPartita(champions, scienze, lettere, arbitro2,
                LocalDateTime.now().minusDays(6), "Campo Scienze", 2, 2, "TERMINATA");
        creaPartita(champions, roma3, scienze, arbitro1,
                LocalDateTime.now().minusDays(3), "Stadio Roma 3", 1, 0, "TERMINATA");
        creaPartita(champions, ingegneria, lettere, arbitro2,
                LocalDateTime.now().plusDays(3), "Campo Ingegneria", null, null, "PROGRAMMATA");

        // --- COMMENTO DI ESEMPIO ---
        Utente user = utenteService.findByUsername("user");
        if (user != null && p1 != null) {
            Commento c = new Commento();
            c.setTesto("Che partita! Totti sempre un fenomeno.");
            commentoService.crea(c, p1, user);
        }

        System.out.println("--- Popolamento completato ---");
    }

    private Squadra creaSquadra(String nome, String citta, int annoFondazione, String colore, Torneo torneo) {
        Squadra s = new Squadra();
        s.setNome(nome);
        s.setCitta(citta);
        s.setAnnoFondazione(annoFondazione);
        s.setColoreMaglia(colore);
        s.setTornei(new ArrayList<>());
        s.getTornei().add(torneo);
        squadraService.save(s);
        return s;
    }

    private void creaGiocatore(String nome, String cognome, String ruolo, int altezza,
                               LocalDate nascita, Squadra squadra) {
        Giocatore g = new Giocatore();
        g.setNome(nome);
        g.setCognome(cognome);
        g.setRuolo(ruolo);
        g.setAltezza(altezza);
        g.setDataNascita(nascita);
        g.setSquadra(squadra);
        giocatoreService.save(g);
    }

    private Arbitro creaArbitro(String nome, String cognome, String codice) {
        Arbitro a = new Arbitro();
        a.setNome(nome);
        a.setCognome(cognome);
        a.setCodiceArbitrale(codice);
        arbitroService.save(a);
        return a;
    }

    private Partita creaPartita(Torneo torneo, Squadra casa, Squadra trasferta, Arbitro arbitro,
                                LocalDateTime dataOra, String luogo, Integer golCasa, Integer golTrasferta,
                                String stato) {
        Partita p = new Partita();
        p.setTorneo(torneo);
        p.setSquadraCasa(casa);
        p.setSquadraTrasferta(trasferta);
        p.setArbitro(arbitro);
        p.setDataOra(dataOra);
        p.setLuogo(luogo);
        p.setGoalsHome(golCasa);
        p.setGoalsAway(golTrasferta);
        p.setStato(stato);
        partitaService.save(p);
        return p;
    }
}
