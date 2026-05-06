package it.uniroma3.siw.torneo.repository;

import it.uniroma3.siw.torneo.model.Torneo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TorneoRepository extends CrudRepository<Torneo, Long> {
    // Non devi scrivere nulla qui dentro!
}