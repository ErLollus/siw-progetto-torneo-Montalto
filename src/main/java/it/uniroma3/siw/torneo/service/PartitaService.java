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
     * Calendario partite con TUTTE le associazioni caricate in un'unica query
     * (join fetch): evita il problema delle N+1 query nella vista /partite.
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