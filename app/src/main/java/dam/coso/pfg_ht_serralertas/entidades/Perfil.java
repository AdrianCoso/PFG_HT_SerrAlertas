package dam.coso.pfg_ht_serralertas.entidades;

public class Perfil {

    private long idPerfil;
    private String nombre;
    private int alertaA, alertaC, alertaE, alertaG;

    public Perfil(int idPerfil, String nombre) {
        this.idPerfil = idPerfil;
        this.nombre = nombre;

    }

    public long getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }

    public int getAlertaA() {
        return alertaA;
    }

    public void setAlertaA(int alertaA) {
        this.alertaA = alertaA;
    }

    public int getAlertaC() {
        return alertaC;
    }

    public void setAlertaC(int alertaC) {
        this.alertaC = alertaC;
    }

    public int getAlertaE() {
        return alertaE;
    }

    public void setAlertaE(int alertaE) {
        this.alertaE = alertaE;
    }

    public int getAlertaG() {
        return alertaG;
    }

    public void setAlertaG(int alertaG) {
        this.alertaG = alertaG;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
