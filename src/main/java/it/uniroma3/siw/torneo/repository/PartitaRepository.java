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
    //  Da qui in giù ci sono i metodi che uso per l'analisi sull'N+1 chiesta
    //  nella sezione 8 del PDF. Il caso d'uso che ho preso come esempio è il
    //  calendario delle partite, dove per ogni partita servono torneo,
    //  squadra di casa, squadra in trasferta e arbitro.
    // ==========================================================================

    /**
     * Strategia 1, quella LAZY: è semplicemente il findAll() ereditato da
     * CrudRepository, senza query custom. La lascio scritta qui solo come
     * promemoria: è la strategia "base" con cui confronto le altre due nel
     * test, quella col problema N+1 (una query per le partite + una per
     * ogni relazione che vado a leggere dopo).
     */

    /**
     * Strategia 2: JOIN FETCH scritto a mano. Con questa carico la partita
     * e tutte le sue relazioni con una query sola, invece delle N+1.
     */
    @Query("SELECT DISTINCT p FROM Partita p " +
            "LEFT JOIN FETCH p.torneo " +
            "LEFT JOIN FETCH p.squadraCasa " +
            "LEFT JOIN FETCH p.squadraTrasferta " +
            "LEFT JOIN FETCH p.arbitro")
    List<Partita> findAllWithDettagli();

    /**
     * Strategia 3: uguale alla precedente ma con @EntityGraph invece di
     * scrivermi la query a mano. Il risultato dovrebbe essere lo stesso,
     * solo dichiarato in un altro modo.
     */
    @EntityGraph(attributePaths = {"torneo", "squadraCasa", "squadraTrasferta", "arbitro"})
    @Query("SELECT p FROM Partita p")
    List<Partita> findAllWithEntityGraph();
}
