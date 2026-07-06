package it.uniroma3.siw.torneo.repository;

import it.uniroma3.siw.torneo.model.Giocatore;
import it.uniroma3.siw.torneo.model.Squadra;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

@Repository
public interface GiocatoreRepository extends CrudRepository<Giocatore, Long> {
    
    List<Giocatore> findBySquadra(Squadra squadra);
    @Query("SELECT g FROM Giocatore g WHERE g.cognome = :cognome ORDER BY g.cognome ASC")
    List<Giocatore> findByCognomeOrderedByCognomeAsc(String cognome);
    Page<Giocatore> findByCognomeStartingWithIgnoreCase(String cognome, Pageable pageable);
}