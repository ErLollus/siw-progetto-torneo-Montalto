package it.uniroma3.siw.torneo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Arbitro {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;
    private String cognome;
    private String codiceArbitrale;

    @OneToMany(mappedBy = "arbitro")
    private List<Partita> partiteDirette;

    // --- GETTER E SETTER ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getCodiceArbitrale() { return codiceArbitrale; }
    public void setCodiceArbitrale(String codiceArbitrale) { this.codiceArbitrale = codiceArbitrale; }

    public List<Partita> getPartiteDirette() { return partiteDirette; }
    public void setPartiteDirette(List<Partita> partiteDirette) { this.partiteDirette = partiteDirette; }
}