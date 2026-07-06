package it.uniroma3.siw.torneo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Commento lasciato da un utente registrato ad una partita.
 * Relazioni:
 *  - un commento appartiene ad una sola partita (ManyToOne)
 *  - un commento è scritto da un solo utente (ManyToOne)
 */
@Entity
public class Commento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il testo del commento non può essere vuoto")
    @Size(max = 500, message = "Il commento non può superare i 500 caratteri")
    @Column(nullable = false, length = 500)
    private String testo;

    @Column(nullable = false)
    private LocalDateTime dataCreazione;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "partita_id")
    private Partita partita;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "autore_id")
    private Utente autore;

    // --- GETTER E SETTER ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTesto() { return testo; }
    public void setTesto(String testo) { this.testo = testo; }

    public LocalDateTime getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDateTime dataCreazione) { this.dataCreazione = dataCreazione; }

    public Partita getPartita() { return partita; }
    public void setPartita(Partita partita) { this.partita = partita; }

    public Utente getAutore() { return autore; }
    public void setAutore(Utente autore) { this.autore = autore; }
}
