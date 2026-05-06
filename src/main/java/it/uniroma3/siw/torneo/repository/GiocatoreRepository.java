package it.uniroma3.siw.torneo.repository;

import it.uniroma3.siw.torneo.model.Giocatore;
import it.uniroma3.siw.torneo.model.Squadra;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiocatoreRepository extends CrudRepository<Giocatore, Long> {
    // Non devi scrivere nulla qui dentro!
    public List<Giocatore> findBySquadra(Squadra squadra);
}