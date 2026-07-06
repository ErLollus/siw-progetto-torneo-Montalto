package it.uniroma3.siw.torneo.service;

import it.uniroma3.siw.torneo.model.Arbitro;
import it.uniroma3.siw.torneo.model.Partita;
import it.uniroma3.siw.torneo.repository.ArbitroRepository;
import it.uniroma3.siw.torneo.repository.PartitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArbitroService {

    @Autowired
    private ArbitroRepository arbitroRepository;
    @Autowired
    private PartitaRepository partitaRepository;

    @Transactional
    public void save(Arbitro arbitro) {
        this.arbitroRepository.save(arbitro);
    }
    @Transactional
    public void deleteById(Long id) {
        Arbitro arbitro = this.arbitroRepository.findById(id).orElse(null);
        if (arbitro != null) {
            // Togliamo l'arbitro da tutte le partite che ha diretto (senza cancellare le partite!)
            List<Partita> partiteDirecte = this.partitaRepository.findByArbitro(arbitro);
            for (Partita p : partiteDirecte) {
                p.setArbitro(null);
                this.partitaRepository.save(p);
            }
            this.arbitroRepository.delete(arbitro);
        }
    }

    @Transactional(readOnly = true)
    public List<Arbitro> findAll() {
        List<Arbitro> arbitri = new ArrayList<>();
        this.arbitroRepository.findAll().forEach(arbitri::add);
        return arbitri;
    }

    @Transactional(readOnly = true)
    public Arbitro findById(Long id) {
        return this.arbitroRepository.findById(id).orElse(null);
    }
}