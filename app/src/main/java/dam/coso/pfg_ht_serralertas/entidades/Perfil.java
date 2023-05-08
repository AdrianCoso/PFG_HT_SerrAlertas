package dam.coso.pfg_ht_serralertas.entidades;

public class Perfil {

    private long idPerfil;
    private String nombre;

    public Perfil(int idPerfil, String nombre) {
        this.idPerfil = idPerfil;
        this.nombre = nombre;

    }

    public long getIdPerfil() {
        return idPerfil;
    }

    public String getNombre() { return nombre; }

}
