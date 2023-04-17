package dam.coso.pfg_ht_serralertas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import dam.coso.pfg_ht_serralertas.adapters.PerfilSpinnerAdapter;
import dam.coso.pfg_ht_serralertas.data.DbAlertas;
import dam.coso.pfg_ht_serralertas.entidades.Perfil;

public class MainActivity extends AppCompatActivity {
    private LinearLayout listaAlertas;
    private Spinner spinnerPerfiles;
    private ArrayList<Perfil> listaPerfiles = new ArrayList<>();
    private ArrayList<String> nombresPerfiles = new ArrayList<>();
    private Cursor cursorPerfiles;
    private DbAlertas db;

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
                cargarAlertas(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listaAlertas = (LinearLayout) findViewById(R.id.linear_lista_alertas);


    }

    private void cargarAlertas(int position) {
        listaAlertas.removeAllViews();
        Perfil perfilSeleccionado = listaPerfiles.get(position);
        Cursor alertas = db.mostrarAlertasPorPerfil(perfilSeleccionado.getIdPerfil());
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0);
        params.weight = 1;
        if (alertas.moveToFirst()) {
            int i = 1;
            do {
                View view = inflater.inflate(R.layout.layout_alerta, listaAlertas, false);
                TextView boton = (TextView) view.findViewById(R.id.tv_boton_alerta);
                boton.setText("Botón "+ i);

                TextView nombre = (TextView) view.findViewById(R.id.tv_nombre_alerta);
                nombre.setText(alertas.getString(1));

                CardView color = (CardView) view.findViewById(R.id.cv_color_alerta);
                color.setCardBackgroundColor(alertas.getInt(2));
                view.setLayoutParams(params);
                listaAlertas.addView(view);
                i++;
            } while (alertas.moveToNext());
        }
    }

}