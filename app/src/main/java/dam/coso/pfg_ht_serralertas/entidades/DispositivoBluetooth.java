package dam.coso.pfg_ht_serralertas.entidades;

public class DispositivoBluetooth {
    private String nombreDispositivoBt, direccionMAC;

    public DispositivoBluetooth(String nombreDispositivoBt, String direccionMAC) {
        this.nombreDispositivoBt = nombreDispositivoBt;
        this.direccionMAC = direccionMAC;
    }

    public String getNombreDispositivoBt() {
        return nombreDispositivoBt;
    }

    public String getDireccionMAC() {
        return direccionMAC;
    }
}
