package it.uniroma3.siw.torneo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Partita {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime dataOra;
    private String luogo;
    private Integer goalsHome;
    private Integer goalsAway;
    private String stato; // es. "PROGRAMMATA", "TERMINATA"

    @ManyToOne
    @JoinColumn(name = "torneo_id")
    private Torneo torneo;

    @ManyToOne
    private Squadra squadraCasa;

    @ManyToOne
    private Squadra squadraTrasferta;

    @ManyToOne
    private Arbitro arbitro;

    // --- GETTER E SETTER ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDataOra() { return dataOra; }
    public void setDataOra(LocalDateTime dataOra) { this.dataOra = dataOra; }

    public String getLuogo() { return luogo; }
    public void setLuogo(String luogo) { this.luogo = luogo; }

    public Integer getGoalsHome() { return goalsHome; }
    public void setGoalsHome(Integer goalsHome) { this.goalsHome = goalsHome; }

    public Integer getGoalsAway() { return goalsAway; }
    public void setGoalsAway(Integer goalsAway) { this.goalsAway = goalsAway; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public Torneo getTorneo() { return torneo; }
    public void setTorneo(Torneo torneo) { this.torneo = torneo; }

    public Squadra getSquadraCasa() { return squadraCasa; }
    public void setSquadraCasa(Squadra squadraCasa) { this.squadraCasa = squadraCasa; }

    public Squadra getSquadraTrasferta() { return squadraTrasferta; }
    public void setSquadraTrasferta(Squadra squadraTrasferta) { this.squadraTrasferta = squadraTrasferta; }

    public Arbitro getArbitro() { return arbitro; }
    public void setArbitro(Arbitro arbitro) { this.arbitro = arbitro; }
}