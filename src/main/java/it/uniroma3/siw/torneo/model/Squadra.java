package it.uniroma3.siw.torneo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Squadra {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;

    private Integer annoFondazione;

    private String citta;

    // Ecco la relazione! Una squadra ha una lista di tornei a cui partecipa.
    @ManyToMany
    private List<Torneo> tornei;

    // --- GETTER E SETTER ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getAnnoFondazione() {
        return annoFondazione;
    }

    public void setAnnoFondazione(Integer annoFondazione) {
        this.annoFondazione = annoFondazione;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public List<Torneo> getTornei() {
        return tornei;
    }

    public void setTornei(List<Torneo> tornei) {
        this.tornei = tornei;
    }
    @OneToMany(mappedBy = "squadra")
    private List<Giocatore> giocatori;


    public List<Giocatore> getGiocatori(){
        return giocatori;
    }
    public void setGiocatori(List<Giocatore> giocatori){
        this.giocatori = giocatori;
    }
    // Sotto le altre variabili


    @OneToMany(mappedBy = "squadraCasa")
    private List<Partita> partiteInCasa;

    @OneToMany(mappedBy = "squadraTrasferta")
    private List<Partita> partiteInTrasferta;

    // --- In fondo alla classe aggiungi i Getter e Setter ---


    public List<Partita> getPartiteInCasa() {
        return partiteInCasa;
    }

    public void setPartiteInCasa(List<Partita> partiteInCasa) {
        this.partiteInCasa = partiteInCasa;
    }

    public List<Partita> getPartiteInTrasferta() {
        return partiteInTrasferta;
    }

    public void setPartiteInTrasferta(List<Partita> partiteInTrasferta) {
        this.partiteInTrasferta = partiteInTrasferta;
    }
}