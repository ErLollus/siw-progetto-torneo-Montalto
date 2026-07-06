package it.uniroma3.siw.torneo.service;

import it.uniroma3.siw.torneo.model.Giocatore;
import it.uniroma3.siw.torneo.model.Squadra;
import it.uniroma3.siw.torneo.model.Torneo;
import it.uniroma3.siw.torneo.repository.GiocatoreRepository;
import it.uniroma3.siw.torneo.repository.SquadraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class SquadraService {

    @Autowired
    private SquadraRepository squadraRepository;
    @Autowired
    private GiocatoreRepository giocatoreRepository;

    @Transactional
    public void save(Squadra squadra) {
        this.squadraRepository.save(squadra);
    }

    @Transactional(readOnly = true)
    public Squadra findById(Long id) {
        return this.squadraRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Squadra> findAll() {
        List<Squadra> squadre = new ArrayList<>();
        this.squadraRepository.findAll().forEach(squadre::add);
        return squadre;
    }
    @Transactional(readOnly = true)
    public List<Squadra> orderByNomeAsc() {
        return this.squadraRepository.orderByNomeAsc();
    }
    @Transactional(readOnly = true)
    public long count() {
        return this.squadraRepository.count();
    }
    @Transactional(readOnly = true)
    public List<Squadra> cerca(String q){
        if(q==null || q.isBlank()) return findAll();
        return this.squadraRepository.findByNomeContainingIgnoreCaseOrCittaContainingIgnoreCase(q,q);
    }
    @Transactional
    public void deleteById(Long id) {
        Squadra squadra = this.squadraRepository.findById(id).orElse(null);

        if (squadra != null) {
            // 1. Trova tutti i giocatori che appartengono a questa squadra
            // (Assumendo che in Giocatore ci sia un campo 'squadra')
            List<Giocatore> giocatori = this.giocatoreRepository.findBySquadra(squadra);

            for (Giocatore g : giocatori) {
                g.setSquadra(null); // Svincoliamo il giocatore
                this.giocatoreRepository.save(g);
            }

            // 2. Rimuovi la squadra dai tornei (gestione ManyToMany)
            // Se hai la lista tornei nella squadra:
            for (Torneo t : squadra.getTornei()) {
                t.getSquadre().remove(squadra);
            }

            // 3. Ora puoi eliminare la squadra in sicurezza
            this.squadraRepository.delete(squadra);
        }
    }
}