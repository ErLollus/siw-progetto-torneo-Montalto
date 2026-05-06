package it.uniroma3.siw.torneo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Giocatore {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;
    private String cognome;
    private LocalDate dataNascita;
    private String ruolo;
    private Integer altezza;

    // Relazione: Molti giocatori appartengono a una sola squadra
    @ManyToOne
    private Squadra squadra;

    // --- GETTER E SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public LocalDate getDataNascita() { return dataNascita; }
    public void setDataNascita(LocalDate dataNascita) { this.dataNascita = dataNascita; }

    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }

    public Integer getAltezza() { return altezza; }
    public void setAltezza(Integer altezza) { this.altezza = altezza; }

    public Squadra getSquadra() { return squadra; }
    public void setSquadra(Squadra squadra) { this.squadra = squadra; }
}