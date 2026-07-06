package it.uniroma3.siw.torneo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Qui configuro la sicurezza dell'app (autenticazione e autorizzazione),
 * come chiesto nella sezione 5 del PDF.
 *
 * Ho due ruoli:
 *  - ADMIN: può gestire tutto (creare/modificare/cancellare le entità)
 *  - USER: può solo usare le funzioni per utenti registrati (i commenti)
 *
 * Per l'autenticazione uso {@link TorneoUserDetailsService} (che va a
 * prendere l'Utente dal database) insieme a {@link BCryptPasswordEncoder}.
 * Spring, trovando questi due bean, si configura da solo il
 * DaoAuthenticationProvider (l'ho scoperto leggendo la documentazione,
 * non l'ho dovuto scrivere a mano).
 */
@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // pagine pubbliche e risorse statiche (css, js, immagini caricate...)
                        .requestMatchers("/", "/index", "/css/**", "/js/**", "/webjars/**",
                                "/uploads/**", "/login", "/register", "/error").permitAll()
                        // l'API REST usata dalla classifica in React, è di sola lettura quindi la lascio pubblica
                        .requestMatchers("/api/**").permitAll()
                        // funzionalità pubbliche, sezione 4.1 del PDF: solo lettura, niente login richiesto
                        .requestMatchers("/tornei", "/torneo/*", "/torneo/*/classifica",
                                "/torneo/*/classifica-react", "/squadre", "/squadra/*",
                                "/giocatori", "/partite", "/arbitri").permitAll()
                        // tutto quello sotto /admin è roba solo per l'ADMIN (sezione 4.3 del PDF)
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // il resto (dettaglio partita, commenti - sezione 4.2) richiede almeno il login
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
