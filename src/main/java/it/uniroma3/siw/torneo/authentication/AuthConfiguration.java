package it.uniroma3.siw.torneo.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class AuthConfiguration {

    @Autowired
    private DataSource dataSource;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabilitiamo CSRF per semplicità nelle API
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/index", "/css/**", "/js/**", "/api/**", "/torneo/**").permitAll() // URL PUBBLICI
                        .requestMatchers("/admin/**").authenticated() // Solo admin per le modifiche
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.permitAll())
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .authoritiesByUsernameQuery("SELECT username, ruolo FROM users WHERE username=?")
                .usersByUsernameQuery("SELECT username, password, 1 as enabled FROM users WHERE username=?");
    }
}