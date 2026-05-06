package it.uniroma3.siw.torneo.repository;

import it.uniroma3.siw.torneo.model.Arbitro;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArbitroRepository extends CrudRepository<Arbitro, Long> {
    // Non devi scrivere nulla qui dentro!
}