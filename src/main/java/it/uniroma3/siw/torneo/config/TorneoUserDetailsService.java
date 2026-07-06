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
 * Questa classe dice a Spring Security come recuperare un utente: va a
 * cercarlo nel database tramite la nostra entità Utente. Il ruolo che ho
 * salvato (USER o ADMIN) lo trasformo in ROLE_USER / ROLE_ADMIN, che è il
 * formato che vuole Spring per poter usare hasRole(...) dopo.
 */
@Service
public class TorneoUserDetailsService implements UserDetailsService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente utente = this.utenteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));

        String ruolo = (utente.getRuolo() == null) ? "USER" : utente.getRuolo().name();

        return User.builder()
                .username(utente.getUsername())
                .password(utente.getPassword())
                .roles(ruolo) // il prefisso ROLE_ lo aggiunge da solo, non serve scriverlo io
                .build();
    }
}
