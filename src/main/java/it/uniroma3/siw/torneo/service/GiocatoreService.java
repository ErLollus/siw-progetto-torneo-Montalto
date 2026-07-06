package it.uniroma3.siw.torneo.service;

import it.uniroma3.siw.torneo.model.Giocatore;
import it.uniroma3.siw.torneo.repository.GiocatoreRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class GiocatoreService {

    @Autowired
    private GiocatoreRepository giocatoreRepository;
    @Transactional(readOnly = true)
    public Page<Giocatore> cerca(String cognome,Pageable pageable){
        return this.giocatoreRepository.findByCognomeStartingWithIgnoreCase(cognome == null ? "" : cognome,pageable);
    }
    @Transactional
    public void save(Giocatore giocatore) {
        this.giocatoreRepository.save(giocatore);
    }
    @Transactional(readOnly = true)
    public long count() {
        return this.giocatoreRepository.count();
    }
    @Transactional(readOnly = true)
    public List<Giocatore> findAll() {
        List<Giocatore> giocatori = new ArrayList<>();
        this.giocatoreRepository.findAll().forEach(giocatori::add);
        return giocatori;
    }
    @Transactional
    public void deleteById(Long id) {
        this.giocatoreRepository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public List<Giocatore> findByCognomeOrderedByCognomeAsc(String cognome) {
        return this.giocatoreRepository.findByCognomeOrderedByCognomeAsc(cognome);
    }
    
    
    @Transactional(readOnly = true)
    public Giocatore findById(Long id) {
        return this.giocatoreRepository.findById(id).orElse(null);
    }
}