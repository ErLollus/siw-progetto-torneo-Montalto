package it.uniroma3.siw.torneo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.util.List;

@Entity
public class Squadra {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il nome della squadra è obbligatorio")
    @Column(nullable = false)
    private String nome;

    private String coloreMaglia;

    @NotNull(message = "L'anno di fondazione è obbligatorio")
    @Min(value = 1800, message = "L'anno di fondazione non è valido")
    private Integer annoFondazione;

    @NotBlank(message = "La città è obbligatoria")
    private String citta;

    // Ecco la relazione! Una squadra ha una lista di tornei a cui partecipa.
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Torneo> tornei;

    // URL esterno oppure nome del file caricato nella cartella di upload, null se assente
    private String immagine;

    public String getImmagine() { return immagine; }
    public void setImmagine(String immagine) { this.immagine = immagine; }

    // --- GETTER E SETTER ---
    public String getColoreMaglia() {
        return coloreMaglia;
    }

    public void setColoreMaglia(String coloreMaglia) {
        this.coloreMaglia = coloreMaglia;
    }

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
    @OneToMany(mappedBy = "squadra", fetch = FetchType.LAZY)
    private List<Giocatore> giocatori;


    public List<Giocatore> getGiocatori(){
        return giocatori;
    }
    public void setGiocatori(List<Giocatore> giocatori){
        this.giocatori = giocatori;
    }
    // Sotto le altre variabili


    @OneToMany(mappedBy = "squadraCasa", fetch = FetchType.LAZY)
    private List<Partita> partiteInCasa;

    @OneToMany(mappedBy = "squadraTrasferta", fetch = FetchType.LAZY)
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