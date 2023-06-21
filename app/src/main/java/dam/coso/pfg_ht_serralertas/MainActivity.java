package dam.coso.pfg_ht_serralertas;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.gridlayout.widget.GridLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import dam.coso.pfg_ht_serralertas.adapters.PerfilSpinnerAdapter;
import dam.coso.pfg_ht_serralertas.data.DbAlertas;
import dam.coso.pfg_ht_serralertas.entidades.Alerta;
import dam.coso.pfg_ht_serralertas.entidades.Perfil;
import dam.coso.pfg_ht_serralertas.servicios.BtService;

public class MainActivity extends AppCompatActivity {
    //private LinearLayout linearListaAlertas;

    private GridLayout gridListaAlertas;
    private Spinner spinnerPerfiles;
    private ArrayList<Perfil> listaPerfiles = new ArrayList<Perfil>();
    private ArrayList<Alerta> listaAlertas = new ArrayList<Alerta>();
    int idPerfilSeleccionado;
    private String TAG = "MainActivity";
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_main);

        crearCanalNotificacion();

        // Obtener índice de perfil seleccionado por preferencias
        pref = getApplicationContext().getSharedPreferences("DATOS", MODE_PRIVATE);
        idPerfilSeleccionado = pref.getInt("perfilSeleccionado", 1);
        boolean btConectado = pref.getBoolean("btConectado", true);

        if (btFuncionando(BtService.class)){
            Log.d(TAG, "Bt funcionando, no se inicia el servicio");
        } else if (!btConectado){
            Log.d(TAG, "Bt desconectado por el usuario");
        } else {
            Intent intentServicioBt = new Intent(getApplicationContext(), BtService.class);
            startService(intentServicioBt);
            Log.d(TAG, "Iniciado bt automáticamente");
        }

        DbAlertas dbAlertas = new DbAlertas(getApplicationContext());

        dbAlertas.cargarPerfilesALista(listaPerfiles);
        dbAlertas.cerrarBD();
        int indicePerfilSeleccionado = obtenerIndicePerfil(idPerfilSeleccionado);

        //linearListaAlertas = (LinearLayout) findViewById(R.id.linear_lista_alertas);
        gridListaAlertas = (GridLayout) findViewById(R.id.grid_alertas);

        // Cargar spinner
        PerfilSpinnerAdapter adapter = new PerfilSpinnerAdapter(listaPerfiles);
        spinnerPerfiles = findViewById(R.id.spinner_perfiles);
        spinnerPerfiles.setAdapter(adapter);
        spinnerPerfiles.setSelection(indicePerfilSeleccionado);
        spinnerPerfiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long idPerfil = listaPerfiles.get(position).getIdPerfil();

                pref.edit().putInt("perfilSeleccionado", (int) idPerfil).apply();
                DbAlertas db = new DbAlertas(getApplicationContext());
                db.mostrarAlertasPorPerfil(idPerfil, listaAlertas);
                db.cerrarBD();
                cargarAlertas();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



    }

    /**
     * Crea un canal de notificaciones para mostrar que el dispositivo seleccionado está conectado.
     */
    private void crearCanalNotificacion() {
        // crear la notificación con un canal propio
        CharSequence name = "Notificación de conexión";
        String description = "Informa de la conexión Bluetooth con la botonera";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel("mi_canal_id", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private boolean btFuncionando(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private int obtenerIndicePerfil(int idPerfilSeleccionado) {
        int indice = 0;
        for (Perfil perfil :
                listaPerfiles) {
            if (perfil.getIdPerfil() == idPerfilSeleccionado) indice = listaPerfiles.indexOf(perfil);
        }
        return indice;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent i;
        switch (id) {
            case R.id.MnOpBluetooth:
                Log.d(TAG, "Pulsada opción bluetooth" );
                // Crear el intent para ir a la actividad que gestiona el bluetooth
                i = new Intent(this, BtConfigActivity.class);
                startActivity(i);
                return true;

            case R.id.MnOpProfile:
                Log.d(TAG,"Pulsada opción perfiles");
                // Crear el intent para ir a la actividad con un listado de los perfiles
                i = new Intent(this, PerfilesActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cargarAlertas() {
        int i = 0;
        gridListaAlertas.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        TypedArray arrPictogramas = getResources().obtainTypedArray(R.array.array_pictogramas);

        for (Alerta alerta :
                listaAlertas) {
            View view = inflater.inflate(R.layout.layout_alerta, gridListaAlertas, false);

            TextView boton = (TextView) view.findViewById(R.id.tv_boton_alerta);
            boton.setText("Botón "+ (i+1));

            TextView nombre = (TextView) view.findViewById(R.id.tv_nombre_alerta);
            nombre.setText(alerta.getTexto());

            ImageView ivPictograma = (ImageView) view.findViewById(R.id.iv_imagen_alerta);
            String uriImagen = alerta.getRutaImagen();
            if (uriImagen.equals("")) {
                Glide.with(this).load(arrPictogramas.getDrawable(i)).fitCenter().into(ivPictograma);
            } else {
                Glide.with(this).load(uriImagen).into(ivPictograma);
            }

            CardView color = (CardView) view.findViewById(R.id.vistaPreviaColor);
            color.setCardBackgroundColor(alerta.getColor());

            int finalI = i + 1;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), EditarAlertaActivity.class);
                    intent.putExtra("idAlerta", alerta.getId());
                    intent.putExtra("iBoton", finalI);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            });
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.height = 0;
            params.width = 0;
            int columnaActual = i % gridListaAlertas.getColumnCount();
            int filaActual = i / gridListaAlertas.getColumnCount();
            params.columnSpec = GridLayout.spec(columnaActual,1,1);
            params.rowSpec = GridLayout.spec(filaActual,1,1);
            view.setLayoutParams(params);
            gridListaAlertas.addView(view);
            i++;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

}