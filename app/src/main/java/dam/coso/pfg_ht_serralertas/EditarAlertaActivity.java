package dam.coso.pfg_ht_serralertas;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import dam.coso.pfg_ht_serralertas.data.DbAlertas;
import dam.coso.pfg_ht_serralertas.entidades.Alerta;
import yuku.ambilwarna.AmbilWarnaDialog;

public class EditarAlertaActivity extends AppCompatActivity {
    private static final String TAG = "EditarAlertaActivity";
    Alerta alerta;
    View vistaPreviaColor;
    int colorDefecto;
    DbAlertas db;
    private ActivityResultLauncher<Intent> lanzadorSelectorFotos;
    private ActivityResultLauncher<Intent> lanzadorSelectorTonos;
    private ImageView vistaPreviaPictograma;
    private TextView tvNombreTono;
    private String nombreTono;
    private Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_alerta);

        // Obtener los datos de la alerta para inicializar componentes
        int idAlerta = getIntent().getIntExtra("idAlerta", -1);
        int iBoton = getIntent().getIntExtra("iBoton", -1);

        // Abrir base de datos
        db = new DbAlertas(getApplicationContext());

        // Instanciar la alerta seleccionada mediante la base de datos
        alerta = db.mostrarAlerta(idAlerta);

        // Componente para escribir el texto de alerta
        EditText etTextoAlerta = (EditText) findViewById(R.id.et_texto_alerta);
        etTextoAlerta.setText(alerta.getTexto());

        // Componente para seleccionar el color de fondo
        colorDefecto = alerta.getColor();
        vistaPreviaColor = (View) findViewById(R.id.vistaPreviaColor);
        vistaPreviaColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSelectorColor();
            }
        });
        vistaPreviaColor.setBackgroundColor(alerta.getColor());

        // Componente para seleccionar el pictograma
        vistaPreviaPictograma = (ImageView) findViewById(R.id.vistaPreviaPicto);
        String uriPictograma = alerta.getRutaImagen();
        if (uriPictograma.equals("")) {
            Glide.with(this).load(getResources().obtainTypedArray(R.array.array_pictogramas).getDrawable(iBoton-1)).into(vistaPreviaPictograma);
        } else{
            Glide.with(this).load(uriPictograma).into(vistaPreviaPictograma);

        }
        //Crear la selección del pictograma
        lanzadorSelectorFotos = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        //Realizamos la operación de cargar la imagen
                        Uri uriImagenSeleccionada = data.getData();
                        getContentResolver().takePersistableUriPermission(uriImagenSeleccionada, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        alerta.setRutaImagen(uriImagenSeleccionada.toString());
                        vistaPreviaPictograma.setImageURI(uriImagenSeleccionada);

                    }
                }
        );
        vistaPreviaPictograma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSelectorImagen();
            }
        });

        // Componente para seleccionar el sonido de la alerta
        tvNombreTono = (TextView) findViewById(R.id.tvNombreTono);
        Uri uriTonoActual;
        if (alerta.getRutaSonido().equals("")) {
            uriTonoActual = RingtoneManager.getActualDefaultRingtoneUri(EditarAlertaActivity.this, RingtoneManager.TYPE_ALARM);
        } else {
            uriTonoActual = Uri.parse(alerta.getRutaSonido());
        }
        ringtone = RingtoneManager.getRingtone(EditarAlertaActivity.this, uriTonoActual);
        nombreTono = ringtone.getTitle(EditarAlertaActivity.this);
        tvNombreTono.setText(nombreTono);
        Button btnSonido = (Button) findViewById(R.id.btnSeleccionAudio);
        lanzadorSelectorTonos = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri uriAudio = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

                        ringtone = RingtoneManager.getRingtone(EditarAlertaActivity.this, uriAudio);
                        //getContentResolver().takePersistableUriPermission(uriAudio, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        alerta.setRutaSonido(uriAudio.toString());
                        nombreTono = RingtoneManager.getRingtone(EditarAlertaActivity.this, uriAudio).getTitle(EditarAlertaActivity.this);
                        tvNombreTono.setText(nombreTono);
                    }
                }
        );
        btnSonido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSelectorArchivoAudio();
            }
        });
        // Botón para probar el sonido seleccionado
        Button btnReproducir = (Button) findViewById(R.id.btn_reproducir_audio);
        btnReproducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ringtone.isPlaying()) {
                    ringtone.stop();
                } else {
                    ringtone.play();
                }

            }
        });

        // Funcionalidad para guardar la alerta modificada
        Button botonGuardar = (Button) findViewById(R.id.btnGuardar);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alerta.setTexto(String.valueOf(etTextoAlerta.getText()));
                int id = db.modificarAlerta(alerta);
                if (id == 1) {
                    Intent intent = new Intent(EditarAlertaActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Log.d(TAG, "Alerta modificada");
                } else {
                    Toast.makeText(EditarAlertaActivity.this, "Error al modificar la alerta", Toast.LENGTH_LONG);
                }

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ringtone.isPlaying()){
            ringtone.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone.isPlaying()) {
            ringtone.stop();
        }
    }

    private void abrirSelectorArchivoAudio() {
        final Uri tonoActual;
        if (alerta.getRutaSonido().equals("")) {
            tonoActual = RingtoneManager.getActualDefaultRingtoneUri(EditarAlertaActivity.this, RingtoneManager.TYPE_ALARM);
        } else {
            tonoActual = Uri.parse(alerta.getRutaSonido());
        }
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Seleccione un tono de alerta");
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, tonoActual);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        lanzadorSelectorTonos.launch(intent);

    }

    private void abrirSelectorImagen() {
        Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        lanzadorSelectorFotos.launch(i);
    }

    private void abrirSelectorColor() {
        final AmbilWarnaDialog colorPickerDialogue = new AmbilWarnaDialog(this, colorDefecto,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // Dejamos este método vacío y el selector se cierra automáticamente al tocar cancelar

                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        // change the mDefaultColor to
                        // change the GFG text color as
                        // it is returned when the OK
                        // button is clicked from the
                        // color picker dialog
                         colorDefecto = color;

                        // now change the picked color
                        // preview box to mDefaultColor
                        vistaPreviaColor.setBackgroundColor(colorDefecto);
                        alerta.setColor(colorDefecto);
                    }
                });
        colorPickerDialogue.show();
    }

}