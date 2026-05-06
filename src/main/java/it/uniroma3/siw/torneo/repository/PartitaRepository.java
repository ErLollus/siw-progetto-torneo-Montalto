package it.uniroma3.siw.torneo.repository;

import it.uniroma3.siw.torneo.model.Arbitro;
import it.uniroma3.siw.torneo.model.Partita;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface PartitaRepository extends CrudRepository<Partita, Long> {

    // AGGIUNGI QUESTA RIGA:
    public List<Partita> findByArbitro(Arbitro arbitro);

}