package it.uniroma3.siw.torneo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // Usiamo 'users' perché 'user' è parola riservata in SQL
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String password;
    private String ruolo; // es. "ADMIN", "USER"

    // --- GETTER E SETTER ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
}