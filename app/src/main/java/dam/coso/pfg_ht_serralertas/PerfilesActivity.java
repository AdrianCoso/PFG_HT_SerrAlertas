package dam.coso.pfg_ht_serralertas;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import dam.coso.pfg_ht_serralertas.adapters.PerfilesListAdapter;
import dam.coso.pfg_ht_serralertas.data.DbAlertas;
import dam.coso.pfg_ht_serralertas.entidades.Perfil;

public class PerfilesActivity extends AppCompatActivity {
    ArrayList<Perfil> listaPerfiles = new ArrayList<>();
    private String TAG = "PerfilesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_perfiles);

        // Inicializar la lista
        ListView lvPerfiles = (ListView) findViewById(R.id.lv_perfiles);

        // Abrir la base de datos
        DbAlertas db = new DbAlertas(getApplicationContext());

        db.cargarPerfilesALista(listaPerfiles);
        db.cerrarBD();

        PerfilesListAdapter adapter = new PerfilesListAdapter(listaPerfiles);
        lvPerfiles.setAdapter(adapter);

        // Funcionalidad del botón para agregar perfiles
        FloatingActionButton fabAgregarPerfil = (FloatingActionButton) findViewById(R.id.fab_agregar_perfil);
        fabAgregarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoNuevoPerfil();
            }
        });
    }

    /**
     * Muestra un cuadro de diálogo para agregar un nuevo perfil.
     * El perfil se crea con el nombre seleccionado por el usuario y cuatro alertas predeterminadas.
     */
    private void mostrarDialogoNuevoPerfil() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View vistaDialogo = inflater.inflate(R.layout.layout_dialogo_nuevo_perfil, null);
        builder.setView(vistaDialogo);
        final EditText etNombreNuevoPerfil = (EditText) vistaDialogo.findViewById(R.id.et_nombre_nuevo_perfil);
        builder.setPositiveButton(R.string.btn_agregar_nuevo_perfil, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombrePerfil = etNombreNuevoPerfil.getText().toString();

                DbAlertas db = new DbAlertas(getApplicationContext());
                db.insertarPerfil(nombrePerfil);
                db.cargarPerfilesALista(listaPerfiles);
                db.cerrarBD();
            }
        });
        builder.setNegativeButton(R.string.btn_cancelar_nuevo_perfil, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No se hace nada. Simplemente se cierra el diálogo.
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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