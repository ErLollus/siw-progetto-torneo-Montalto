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
     * Prende i commenti di una partita. Ho aggiunto l'EntityGraph per
     * caricare subito anche l'autore, altrimenti per stampare "commento
     * di <username>" in vista Hibernate farebbe una query in più per ogni
     * commento (il solito N+1).
     */
    @EntityGraph(attributePaths = {"autore"})
    List<Commento> findByPartitaOrderByDataCreazioneDesc(Partita partita);

    long countByPartita(Partita partita);
}
