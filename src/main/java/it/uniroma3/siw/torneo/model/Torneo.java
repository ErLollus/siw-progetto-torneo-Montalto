package it.uniroma3.siw.torneo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.util.List;

@Entity
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il nome del torneo è obbligatorio")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "L'anno è obbligatorio")
    @Min(value = 1900, message = "L'anno non è valido")
    @Column(nullable = false)
    private Integer anno;

    @Size(max = 1000, message = "La descrizione è troppo lunga")
    @Column(length = 1000)
    private String descrizione;

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

    public Integer getAnno() {
        return anno;
    }

    public void setAnno(Integer anno) {
        this.anno = anno;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    // Aggiungi questo sotto "descrizione"
    @ManyToMany(mappedBy = "tornei", fetch = FetchType.LAZY)
    private List<Squadra> squadre;
    public List<Squadra> getSquadre() {
        return squadre;
    }

    public void setSquadre(List<Squadra> squadre) {
        this.squadre = squadre;
    }
    // Sotto le altre variabili (nome, anno, ecc.)
    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Partita> partite;

    // --- In fondo alla classe aggiungi i Getter e Setter ---
    public List<Partita> getPartite() {
        return partite;
    }

    public void setPartite(List<Partita> partite) {
        this.partite = partite;
    }
}
