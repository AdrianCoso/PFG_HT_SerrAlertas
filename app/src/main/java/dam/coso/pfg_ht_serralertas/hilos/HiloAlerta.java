package dam.coso.pfg_ht_serralertas.hilos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import dam.coso.pfg_ht_serralertas.MostrarAlertaActivity;
import dam.coso.pfg_ht_serralertas.data.DbAlertas;
import dam.coso.pfg_ht_serralertas.entidades.Alerta;

public class HiloAlerta extends Thread{
    Context contexto;
    String mensaje;

    public HiloAlerta(Context contexto, String mensaje) {
        this.contexto = contexto;
        this.mensaje = mensaje;

    }

    /**
     * Localiza la alerta dependiendo del perfil que esté a la escucha y lanza un intent para mostrarla
     */
    @Override
    public void run() {
        DbAlertas db = new DbAlertas(contexto);
        SharedPreferences preferences = contexto.getSharedPreferences("DATOS", Context.MODE_PRIVATE);
        int idPerfil = preferences.getInt("perfilSeleccionado", 1);
        Alerta alerta = db.mostrarAlertaRecibida(idPerfil, mensaje);
        Intent intentAlerta = new Intent(contexto, MostrarAlertaActivity.class);
        intentAlerta.putExtra("mensaje", mensaje);
        intentAlerta.putExtra("texto", alerta.getTexto());
        intentAlerta.putExtra("color", alerta.getColor());
        intentAlerta.putExtra("imagen", alerta.getRutaImagen());
        intentAlerta.putExtra("sonido", alerta.getRutaSonido());
        intentAlerta.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        contexto.startActivity(intentAlerta);

    }
}
