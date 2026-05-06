package it.uniroma3.siw.torneo.service;

import it.uniroma3.siw.torneo.model.Utente;
import it.uniroma3.siw.torneo.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Transactional
    public void save(Utente utente) {
        utente.setPassword(passwordEncoder.encode(utente.getPassword()));
        this.utenteRepository.save(utente);
    }

    public Utente findByUsername(String username) {
        // Nota: Qui CrudRepository standard non ha findByUsername,
        // lo aggiungeremo se servirà per il login, per ora lasciamo la base.
        return null;
    }
}