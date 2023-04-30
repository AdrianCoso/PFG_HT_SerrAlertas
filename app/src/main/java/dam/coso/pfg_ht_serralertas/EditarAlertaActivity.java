package dam.coso.pfg_ht_serralertas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_alerta);

        int idAlerta = getIntent().getIntExtra("idAlerta", -1);
        int iBoton = getIntent().getIntExtra("iBoton", -1);

        db = new DbAlertas(getApplicationContext());
        alerta = db.mostrarAlerta(idAlerta);

        EditText etTextoAlerta = (EditText) findViewById(R.id.et_texto_alerta);
        etTextoAlerta.setText(alerta.getTexto());


        colorDefecto = alerta.getColor();
        vistaPreviaColor = (View) findViewById(R.id.vistaPreviaColor);
        vistaPreviaColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSelectorColor();
            }
        });
        vistaPreviaColor.setBackgroundColor(alerta.getColor());

        ImageView vistaPreviaPictograma = (ImageView) findViewById(R.id.vistaPreviaPicto);
        String uriPictograma = alerta.getRutaImagen();
        if (uriPictograma.equals("")) {
            Glide.with(this).load(getResources().obtainTypedArray(R.array.array_pictogramas).getDrawable(iBoton-1)).into(vistaPreviaPictograma);
        } else{
            Glide.with(this).load(uriPictograma).into(vistaPreviaPictograma);

        }
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