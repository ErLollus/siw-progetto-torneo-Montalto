package it.uniroma3.siw.torneo.repository;

import it.uniroma3.siw.torneo.model.Squadra;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SquadraRepository extends CrudRepository<Squadra, Long> {
    @Query("SELECT s FROM Squadra s ORDER BY s.nome ASC")
    List<Squadra> orderByNomeAsc();
    List<Squadra> findByNomeContainingIgnoreCaseOrCittaContainingIgnoreCase(String nome, String citta);
}