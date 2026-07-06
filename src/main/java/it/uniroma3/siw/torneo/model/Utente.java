package it.uniroma3.siw.torneo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users") // ho dovuto chiamarla "users" perché "user" è una parola riservata in SQL, altrimenti dava errore
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // campi che ho aggiunto dopo rispetto alla prima versione
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

    @Enumerated(EnumType.STRING)
    private Ruolo ruolo;

    // getter e setter

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Ruolo getRuolo() { return ruolo; }
    public void setRuolo(Ruolo ruolo) { this.ruolo = ruolo; }
}