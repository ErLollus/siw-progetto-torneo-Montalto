package it.uniroma3.siw.torneo.service;

import it.uniroma3.siw.torneo.model.Partita;
import it.uniroma3.siw.torneo.model.Squadra;
import it.uniroma3.siw.torneo.model.SquadraClassifica;
import it.uniroma3.siw.torneo.model.Torneo;
import it.uniroma3.siw.torneo.repository.SquadraRepository;
import it.uniroma3.siw.torneo.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service // Dice a Spring: "Questa è una classe di logica"
public class TorneoService {

    @Autowired // Chiede a Spring di portarci il "telecomando" del DB
    private TorneoRepository torneoRepository;
    @Autowired
    private SquadraRepository squadraRepository;

    @Transactional // Usalo per i metodi che salvano o modificano dati
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
            // 1. Prima di cancellare il torneo, andiamo da ogni squadra iscritta...
            for (Squadra squadra : torneo.getSquadre()) {
                // ...e cancelliamo questo torneo dalla lista dei SUOI tornei
                squadra.getTornei().remove(torneo);
                this.squadraRepository.save(squadra); // Salviamo la squadra aggiornata
            }

            // 2. Svuotiamo anche la lista lato Torneo (per sicurezza)
            torneo.getSquadre().clear();

            // 3. Ora che i collegamenti sono tagliati, possiamo demolire il palazzo!
            this.torneoRepository.delete(torneo);
        }

    }
    /**
     * Calcolo della classifica di un torneo: operazione di sola lettura che
     * naviga le associazioni squadre/partite del torneo. Isolamento di default
     * (READ_COMMITTED) sufficiente per una lettura non critica.
     */
    @Transactional(readOnly = true)
    public List<SquadraClassifica> calcolaClassifica(Long torneoId) {
        Torneo torneo = this.torneoRepository.findById(torneoId).orElse(null);
        if (torneo == null) return new ArrayList<>();

        Map<Long, SquadraClassifica> mappaClassifica = new HashMap<>();

        // Inizializziamo la classifica con tutte le squadre ISCRITTE al torneo
        for (Squadra s : torneo.getSquadre()) {
            mappaClassifica.put(s.getId(), new SquadraClassifica(s.getId(),s.getNome()));
        }

        // Calcoliamo i punti
        for (Partita p : torneo.getPartite()) {
            // Usiamo equalsIgnoreCase per evitare errori tra "terminata" e "TERMINATA"
            if (p.getStato() != null && p.getStato().equalsIgnoreCase("TERMINATA")) {

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

        // Ordinamento: Punti (decrescente), poi Differenza Reti
        classifica.sort((a, b) -> {
            if (b.getPunti() != a.getPunti()) return b.getPunti() - a.getPunti();
            return (b.getGolFatti() - b.getGolSubiti()) - (a.getGolFatti() - a.getGolSubiti());
        });

        return classifica;
    }
}