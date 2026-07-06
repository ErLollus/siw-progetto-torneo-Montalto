package it.uniroma3.siw.torneo.service;

import it.uniroma3.siw.torneo.model.Partita;
import it.uniroma3.siw.torneo.model.Squadra;
import it.uniroma3.siw.torneo.model.SquadraClassifica;
import it.uniroma3.siw.torneo.model.StatoPartita;
import it.uniroma3.siw.torneo.model.Torneo;
import it.uniroma3.siw.torneo.repository.SquadraRepository;
import it.uniroma3.siw.torneo.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service // dico a Spring che qui dentro c'è la logica di business, non roba di controller o persistenza
public class TorneoService {

    @Autowired // Spring mi inietta il repository, così posso parlare col database
    private TorneoRepository torneoRepository;
    @Autowired
    private SquadraRepository squadraRepository;

    @Transactional // ci va sempre quando il metodo scrive/modifica qualcosa
    public void saveTorneo(Torneo torneo) {
        this.torneoRepository.save(torneo);
    }

    @Transactional(readOnly = true)
    public Torneo findById(Long id) {
        return this.torneoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Torneo> findAll() {
        List<Torneo> tornei = new ArrayList<>();
        this.torneoRepository.findAll().forEach(tornei::add);
        return tornei;
    }
    @Transactional(readOnly = true)
    public long count() {
        return this.torneoRepository.count();
    }
    @Transactional
    public void deleteById(Long id) {
        Torneo torneo = this.torneoRepository.findById(id).orElse(null);

        if (torneo != null) {
            // 1. prima di cancellare il torneo devo staccare il collegamento da ogni squadra iscritta...
            for (Squadra squadra : torneo.getSquadre()) {
                // ...altrimenti Hibernate si lamenta per via della relazione ManyToMany
                squadra.getTornei().remove(torneo);
                this.squadraRepository.save(squadra); // salvo la squadra con la lista tornei aggiornata
            }

            // 2. svuoto anche la lista lato Torneo, giusto per stare tranquillo
            torneo.getSquadre().clear();

            // 3. a questo punto i collegamenti sono tagliati e posso cancellare il torneo senza problemi
            this.torneoRepository.delete(torneo);
        }

    }
    /**
     * Questo metodo calcola la classifica di un torneo, guardando tutte le
     * squadre iscritte e le partite già terminate.
     * Ho messo l'isolamento REPEATABLE_READ (invece di quello di default,
     * READ_COMMITTED) perché mentre calcolo la classifica un admin potrebbe
     * nel frattempo modificare il risultato di una partita: se succedesse a
     * metà calcolo rischierei di leggere due volte la stessa partita con
     * dati diversi, e la classifica verrebbe fuori sbagliata (tipo punti
     * giusti ma differenza reti calcolata su un risultato vecchio). L'ho
     * studiato nel corso di basi di dati, spero di averlo applicato bene.
     */
    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    public List<SquadraClassifica> calcolaClassifica(Long torneoId) {
        Torneo torneo = this.torneoRepository.findById(torneoId).orElse(null);
        if (torneo == null) return new ArrayList<>();

        Map<Long, SquadraClassifica> mappaClassifica = new HashMap<>();

        // parto mettendo in classifica tutte le squadre iscritte, anche quelle a 0 punti
        for (Squadra s : torneo.getSquadre()) {
            mappaClassifica.put(s.getId(), new SquadraClassifica(s.getId(),s.getNome()));
        }

        // scorro tutte le partite e assegno i punti solo per quelle già finite
        for (Partita p : torneo.getPartite()) {
            if (p.getStato() == StatoPartita.TERMINATA) {

                Squadra casa = p.getSquadraCasa();
                Squadra trasferta = p.getSquadraTrasferta();

                if (casa != null && trasferta != null) {
                    SquadraClassifica dtoCasa = mappaClassifica.get(casa.getId());
                    SquadraClassifica dtoTrasferta = mappaClassifica.get(trasferta.getId());

                    if (dtoCasa != null && dtoTrasferta != null) {
                        dtoCasa.aggiorna(p.getGoalsHome(), p.getGoalsAway());
                        dtoTrasferta.aggiorna(p.getGoalsAway(), p.getGoalsHome());
                    }
                }
            }
        }

        List<SquadraClassifica> classifica = new ArrayList<>(mappaClassifica.values());

        // ordino per punti decrescenti, e a parità di punti guardo la differenza reti
        classifica.sort((a, b) -> {
            if (b.getPunti() != a.getPunti()) return b.getPunti() - a.getPunti();
            return (b.getGolFatti() - b.getGolSubiti()) - (a.getGolFatti() - a.getGolSubiti());
        });

        return classifica;
    }
}