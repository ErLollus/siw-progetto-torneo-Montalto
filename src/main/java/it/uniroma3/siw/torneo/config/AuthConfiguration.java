package it.uniroma3.siw.torneo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configurazione di sicurezza (autenticazione + autorizzazione).
 *
 * Ruoli:
 *  - ADMIN: gestione completa (creazione/modifica/eliminazione entità)
 *  - USER : funzionalità per utenti registrati (commenti)
 *
 * L'autenticazione si appoggia a {@link TorneoUserDetailsService} (che carica
 * l'entità Utente dal DB) e a {@link BCryptPasswordEncoder}: Spring configura
 * automaticamente il DaoAuthenticationProvider trovando questi due bean.
 */
@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Risorse statiche e pagine di ingresso
                        .requestMatchers("/", "/index", "/css/**", "/js/**", "/webjars/**",
                                "/uploads/**", "/login", "/register", "/error").permitAll()
                        // API REST di sola lettura (classifica React)
                        .requestMatchers("/api/**").permitAll()
                        // Funzionalità PUBBLICHE (Sezione 4.1 del PDF): sola lettura
                        .requestMatchers("/tornei", "/torneo/*", "/torneo/*/classifica",
                                "/torneo/*/classifica-react", "/squadre", "/squadra/*",
                                "/giocatori", "/partite", "/arbitri").permitAll()
                        // Funzionalità AMMINISTRATIVE (Sezione 4.3): solo ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Tutto il resto (dettaglio partita + commenti, Sezione 4.2)
                        // richiede un utente autenticato
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
