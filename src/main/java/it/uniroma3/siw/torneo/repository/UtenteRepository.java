package it.uniroma3.siw.torneo.repository;

import it.uniroma3.siw.torneo.model.Utente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UtenteRepository extends CrudRepository<Utente, Long> {
    // Non devi scrivere nulla qui dentro!
}