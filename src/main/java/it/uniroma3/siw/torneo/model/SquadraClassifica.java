package it.uniroma3.siw.torneo.model;



public class SquadraClassifica {
    private Long id;
    private String nome;
    private int punti = 0;
    private int giocate = 0;
    private int vittorie = 0;
    private int pareggi = 0;
    private int sconfitte = 0;
    private int golFatti = 0;
    private int golSubiti = 0;

    public SquadraClassifica(Long id, String nome) {
        this.nome = nome;
        this.id = id;
    }

    public void aggiorna(int fatti, int subiti) {
        this.giocate++;
        this.golFatti += fatti;
        this.golSubiti += subiti;
        if (fatti > subiti) {
            this.punti += 3;
            this.vittorie++;
        } else if (fatti == subiti) {
            this.punti += 1;
            this.pareggi++;
        } else {
            this.sconfitte++;
        }
    }

    // getter per tutti i campi, mi servono perché Thymeleaf ci accede tramite questi
    public String getNome() { return nome; }
    public int getPunti() { return punti; }
    public int getGiocate() { return giocate; }
    public int getVittorie() { return vittorie; }
    public int getPareggi() { return pareggi; }
    public int getSconfitte() { return sconfitte; }
    public int getGolFatti() { return golFatti; }
    public int getGolSubiti() { return golSubiti; }
    public int getDiffReti() { return golFatti - golSubiti; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id;}
}
