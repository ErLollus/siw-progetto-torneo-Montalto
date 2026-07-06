package it.uniroma3.siw.torneo.config;

import it.uniroma3.siw.torneo.model.Utente;
import it.uniroma3.siw.torneo.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Carica l'utente dal database (entità Utente) per l'autenticazione.
 * Il ruolo memorizzato ("USER"/"ADMIN") viene mappato sull'authority
 * "ROLE_USER"/"ROLE_ADMIN", così da poter usare hasRole(...) nella
 * configurazione di sicurezza.
 */
@Service
public class TorneoUserDetailsService implements UserDetailsService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente utente = this.utenteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));

        String ruolo = (utente.getRuolo() == null || utente.getRuolo().isBlank())
                ? "USER" : utente.getRuolo();

        return User.builder()
                .username(utente.getUsername())
                .password(utente.getPassword())
                .roles(ruolo) // aggiunge automaticamente il prefisso ROLE_
                .build();
    }
}
