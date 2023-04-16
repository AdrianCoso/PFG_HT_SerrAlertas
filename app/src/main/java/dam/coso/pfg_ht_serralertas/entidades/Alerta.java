package dam.coso.pfg_ht_serralertas.entidades;

public class Alerta {
    private int id;
    private String texto;
    private int color;
    private String rutaImagen, rutaSonido;
    private boolean activa;

    public Alerta(int id, String texto, int color, String rutaImagen, String rutaSonido, boolean activa) {
        this.id = id;
        this.texto = texto;
        this.color = color;
        this.rutaImagen = rutaImagen;
        this.rutaSonido = rutaSonido;
        this.activa = activa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }

    public String getRutaSonido() {
        return rutaSonido;
    }

    public void setRutaSonido(String rutaSonido) {
        this.rutaSonido = rutaSonido;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

}
