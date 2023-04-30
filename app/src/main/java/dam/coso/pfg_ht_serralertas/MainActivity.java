package dam.coso.pfg_ht_serralertas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import dam.coso.pfg_ht_serralertas.adapters.PerfilSpinnerAdapter;
import dam.coso.pfg_ht_serralertas.data.DbAlertas;
import dam.coso.pfg_ht_serralertas.entidades.Alerta;
import dam.coso.pfg_ht_serralertas.entidades.Perfil;

public class MainActivity extends AppCompatActivity {
    private LinearLayout linearListaAlertas;
    private Spinner spinnerPerfiles;
    private ArrayList<Perfil> listaPerfiles = new ArrayList<>();
    private ArrayList<Alerta> listaAlertas = new ArrayList<Alerta>();

    private DbAlertas db;
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DbAlertas(getApplicationContext());

        db.cargarPerfilesALista(listaPerfiles);
        if (listaPerfiles.size() == 0) {
            db.insertarPerfil("Perfil por defecto");
            db.cargarPerfilesALista(listaPerfiles);
        }


        // Cargar spinner
        PerfilSpinnerAdapter adapter = new PerfilSpinnerAdapter(listaPerfiles);
        spinnerPerfiles = findViewById(R.id.spinner_perfiles);
        spinnerPerfiles.setAdapter(adapter);
        spinnerPerfiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                db.mostrarAlertasPorPerfil(listaPerfiles.get(position).getIdPerfil(), listaAlertas);
                cargarAlertas();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        linearListaAlertas = (LinearLayout) findViewById(R.id.linear_lista_alertas);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.MnOpBluetooth:
                Log.d(TAG, "Pulsada opción bluetooth" );
                // Crear el intent para ir a la actividad que gestiona el bluetooth
                Intent i = new Intent(this, BtConfigActivity.class);
                startActivity(i);
                return true;


            case R.id.MnOpNotifications:
                Log.d(TAG, "Pulsada opción notificaciones");
                return true;

            case R.id.MnOpProfile:
                Log.d(TAG,"Pulsada opción perfiles");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void cargarAlertas() {
        int i = 1;
        linearListaAlertas.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        TypedArray arrPictogramas = getResources().obtainTypedArray(R.array.array_pictogramas);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
        params.weight = 1;
        for (Alerta alerta :
                listaAlertas) {
            View view = inflater.inflate(R.layout.layout_alerta, linearListaAlertas, false);

            TextView boton = (TextView) view.findViewById(R.id.tv_boton_alerta);
            boton.setText("Botón "+ i);

            TextView nombre = (TextView) view.findViewById(R.id.tv_nombre_alerta);
            nombre.setText(alerta.getTexto());

            ImageView ivPictograma = (ImageView) view.findViewById(R.id.iv_imagen_alerta);
            String uriImagen = alerta.getRutaImagen();
            if (uriImagen.equals("")) {
                Glide.with(this).load(arrPictogramas.getDrawable(i-1)).fitCenter().into(ivPictograma);
            } else {
                Glide.with(this).load(uriImagen).into(ivPictograma);
            }

            CardView color = (CardView) view.findViewById(R.id.cv_color_alerta);
            color.setCardBackgroundColor(alerta.getColor());
            view.setLayoutParams(params);
            int finalI = i;
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
            linearListaAlertas.addView(view);
            i++;
        }

    }

}