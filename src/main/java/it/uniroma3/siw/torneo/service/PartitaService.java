package it.uniroma3.siw.torneo.service;

import it.uniroma3.siw.torneo.model.Partita;
import it.uniroma3.siw.torneo.repository.PartitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class PartitaService {

    @Autowired
    private PartitaRepository partitaRepository;

    @Transactional
    public void save(Partita partita) {
        this.partitaRepository.save(partita);
    }

    @Transactional(readOnly = true)
    public List<Partita> findAll() {
        List<Partita> partite = new ArrayList<>();
        this.partitaRepository.findAll().forEach(partite::add);
        return partite;
    }

    /**
     * Ritorna il calendario partite con già tutte le relazioni caricate,
     * usando il join fetch: mi serve per non incappare nel problema N+1
     * quando la view /partite mostra squadra, torneo e arbitro di ogni match.
     */
    @Transactional(readOnly = true)
    public List<Partita> findAllConDettagli() {
        return this.partitaRepository.findAllWithDettagli();
    }

    @Transactional(readOnly = true)
    public Partita findById(Long id) {
        return this.partitaRepository.findById(id).orElse(null);
    }
    @Transactional
    public void deleteById(Long id) {
        this.partitaRepository.deleteById(id);
    }
}