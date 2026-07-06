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

    /**
     * Salva un utente nuovo, cifrando prima la password con BCrypt.
     * Ho messo @Transactional perché scrive sul database.
     */
    @Transactional
    public void save(Utente utente) {
        utente.setPassword(passwordEncoder.encode(utente.getPassword()));
        this.utenteRepository.save(utente);
    }

    @Transactional(readOnly = true)
    public Utente findByUsername(String username) {
        return this.utenteRepository.findByUsername(username).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return this.utenteRepository.existsByUsername(username);
    }
}
