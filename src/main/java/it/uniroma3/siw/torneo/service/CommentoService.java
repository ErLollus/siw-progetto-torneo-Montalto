package it.uniroma3.siw.torneo.service;

import it.uniroma3.siw.torneo.model.Commento;
import it.uniroma3.siw.torneo.model.Partita;
import it.uniroma3.siw.torneo.model.Utente;
import it.uniroma3.siw.torneo.repository.CommentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentoService {

    @Autowired
    private CommentoRepository commentoRepository;

    @Transactional(readOnly = true)
    public List<Commento> findByPartita(Partita partita) {
        return this.commentoRepository.findByPartitaOrderByDataCreazioneDesc(partita);
    }

    @Transactional(readOnly = true)
    public Commento findById(Long id) {
        return this.commentoRepository.findById(id).orElse(null);
    }

    /**
     * Crea un nuovo commento associandolo a partita e autore correnti.
     */
    @Transactional
    public void crea(Commento commento, Partita partita, Utente autore) {
        commento.setPartita(partita);
        commento.setAutore(autore);
        commento.setDataCreazione(LocalDateTime.now());
        this.commentoRepository.save(commento);
    }

    /**
     * Aggiorna il testo di un commento SOLO se l'utente richiedente ne è l'autore.
     * Restituisce true se l'aggiornamento è avvenuto.
     */
    @Transactional
    public boolean aggiornaSeAutore(Long commentoId, String nuovoTesto, String username) {
        Commento commento = this.commentoRepository.findById(commentoId).orElse(null);
        if (commento == null || commento.getAutore() == null
                || !commento.getAutore().getUsername().equals(username)) {
            return false;
        }
        commento.setTesto(nuovoTesto);
        this.commentoRepository.save(commento);
        return true;
    }

    /**
     * Elimina un commento SOLO se l'utente richiedente ne è l'autore.
     */
    @Transactional
    public boolean eliminaSeAutore(Long commentoId, String username) {
        Commento commento = this.commentoRepository.findById(commentoId).orElse(null);
        if (commento == null || commento.getAutore() == null
                || !commento.getAutore().getUsername().equals(username)) {
            return false;
        }
        this.commentoRepository.delete(commento);
        return true;
    }

    /**
     * Elimina un commento se l'utente richiedente ne è l'autore, oppure se è un ADMIN
     * (l'ADMIN può eliminare i commenti di chiunque).
     */
    @Transactional
    public boolean elimina(Long commentoId, String username, boolean isAdmin) {
        Commento commento = this.commentoRepository.findById(commentoId).orElse(null);
        if (commento == null) {
            return false;
        }
        boolean autore = commento.getAutore() != null && commento.getAutore().getUsername().equals(username);
        if (!isAdmin && !autore) {
            return false;
        }
        this.commentoRepository.delete(commento);
        return true;
    }

    /**
     * Verifica se l'utente indicato è l'autore del commento (usato dalla UI).
     */
    @Transactional(readOnly = true)
    public boolean isAutore(Long commentoId, String username) {
        Commento commento = this.commentoRepository.findById(commentoId).orElse(null);
        return commento != null && commento.getAutore() != null
                && commento.getAutore().getUsername().equals(username);
    }
}
