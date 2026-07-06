package it.uniroma3.siw.torneo.repository;

import it.uniroma3.siw.torneo.model.Arbitro;
import it.uniroma3.siw.torneo.model.Partita;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PartitaRepository extends CrudRepository<Partita, Long> {

    List<Partita> findByArbitro(Arbitro arbitro);

    // ==========================================================================
    //  Metodi usati per l'analisi sperimentale sulle strategie di accesso ai
    //  dati (Sezione 8 del PDF). Il caso d'uso è "visualizzazione del calendario
    //  delle partite", che per ogni partita naviga verso torneo, squadra di
    //  casa, squadra in trasferta e arbitro.
    // ==========================================================================

    /**
     * STRATEGIA 1 — LAZY (default di CrudRepository.findAll()).
     * Una query per le partite + una query per OGNI associazione navigata
     * successivamente: è il classico problema N+1.
     * (Usa findAll() ereditato — riportato qui solo come riferimento concettuale.)
     */

    /**
     * STRATEGIA 2 — JOIN FETCH esplicito.
     * Carica partite e tutte le associazioni in UNA sola query.
     */
    @Query("SELECT DISTINCT p FROM Partita p " +
            "LEFT JOIN FETCH p.torneo " +
            "LEFT JOIN FETCH p.squadraCasa " +
            "LEFT JOIN FETCH p.squadraTrasferta " +
            "LEFT JOIN FETCH p.arbitro")
    List<Partita> findAllWithDettagli();

    /**
     * STRATEGIA 3 — EntityGraph.
     * Stesso effetto del join fetch ma dichiarato in modo dichiarativo.
     */
    @EntityGraph(attributePaths = {"torneo", "squadraCasa", "squadraTrasferta", "arbitro"})
    @Query("SELECT p FROM Partita p")
    List<Partita> findAllWithEntityGraph();
}
