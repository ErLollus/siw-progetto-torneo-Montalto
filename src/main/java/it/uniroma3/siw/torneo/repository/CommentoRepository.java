package it.uniroma3.siw.torneo.repository;

import it.uniroma3.siw.torneo.model.Commento;
import it.uniroma3.siw.torneo.model.Partita;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentoRepository extends CrudRepository<Commento, Long> {

    /**
     * Commenti di una partita. Usa un EntityGraph per caricare in un colpo solo
     * anche l'autore, evitando il problema delle N+1 query quando in vista
     * mostriamo "commento di <username>".
     */
    @EntityGraph(attributePaths = {"autore"})
    List<Commento> findByPartitaOrderByDataCreazioneDesc(Partita partita);

    long countByPartita(Partita partita);
}
