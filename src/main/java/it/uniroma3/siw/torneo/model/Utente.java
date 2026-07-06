package it.uniroma3.siw.torneo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users") // Usiamo 'users' perché 'user' è parola riservata in SQL
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // --- NUOVI CAMPI AGGIUNTI ---
    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "L'username è obbligatorio")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    @Size(min = 4, message = "La password deve avere almeno 4 caratteri")
    private String password;

    private String ruolo; // es. "ADMIN", "USER"

    // --- GETTER E SETTER ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Getter e Setter per Nome
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    // Getter e Setter per Cognome
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
}