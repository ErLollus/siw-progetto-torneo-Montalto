package it.uniroma3.siw.torneo.service;

import it.uniroma3.siw.torneo.model.Giocatore;
import it.uniroma3.siw.torneo.repository.GiocatoreRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class GiocatoreService {

    @Autowired
    private GiocatoreRepository giocatoreRepository;

    @Transactional
    public void save(Giocatore giocatore) {
        this.giocatoreRepository.save(giocatore);
    }

    public List<Giocatore> findAll() {
        List<Giocatore> giocatori = new ArrayList<>();
        this.giocatoreRepository.findAll().forEach(giocatori::add);
        return giocatori;
    }
    @Transactional
    public void deleteById(Long id) {
        this.giocatoreRepository.deleteById(id);
    }

    public Giocatore findById(Long id) {
        return this.giocatoreRepository.findById(id).orElse(null);
    }
}