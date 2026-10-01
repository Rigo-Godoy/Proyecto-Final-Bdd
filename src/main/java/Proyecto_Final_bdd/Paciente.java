package Proyecto_Final_bdd;

import java.time.LocalDate;

public class Paciente {
    private int idPac;
    private String nomPac;
    private String apPatPac;
    private String apMatPac;
    private LocalDate fecNacim;
    private String genero;
    private String direPac;
    private String telPac;
    private String correoPac;
    private String contEmerTel;
    private String tSangre;
    private String alergias;

    public Paciente(
        String nomPac,
        String apPatPac,
        String apMatPac,
        LocalDate fecNacim,
        String genero,
        String direPac,
        String telPac,
        String correoPac,
        String contEmerTel,
        String tSangre,
        String alergias
    ) {
        this.nomPac = nomPac;
        this.apPatPac = apPatPac;
        this.apMatPac = apMatPac;
        this.fecNacim = fecNacim;
        this.genero = genero;
        this.direPac = direPac;
        this.telPac = telPac;
        this.correoPac = correoPac;
        this.contEmerTel = contEmerTel;
        this.tSangre = tSangre;
        this.alergias = alergias;
    }

    public String getNomPac() {
        return nomPac;
    }

    public String getApPatPac() {
        return apPatPac;
    }

    public String getApMatPac() {
        return apMatPac;
    }

    public LocalDate getFecNacim() {
        return fecNacim;
    }

    public String getGenero() {
        return genero;
    }

    public String getDirePac() {
        return direPac;
    }

    public String getTelPac() {
        return telPac;
    }

    public String getCorreoPac() {
        return correoPac;
    }

    public String getContEmerTel() {
        return contEmerTel;
    }

    public String getTSangre() {
        return tSangre;
    }

    public String getAlergias() {
        return alergias;
    }

    public int getIdPac() {
        return idPac;
    }

    public void setIdPac(int idPac) {
        this.idPac = idPac;
    }

    public void eliminar(int id) {
    }
}
