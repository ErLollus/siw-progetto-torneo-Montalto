package it.uniroma3.siw.torneo.repository;

import it.uniroma3.siw.torneo.model.Squadra;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SquadraRepository extends CrudRepository<Squadra, Long> {
    // Non devi scrivere nulla qui dentro!
}