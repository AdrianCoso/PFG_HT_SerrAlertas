package dam.coso.pfg_ht_serralertas;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

public class MostrarAlertaActivity extends AppCompatActivity {
    private TextView mensaje;
    private ConstraintLayout fondo;
    private ImageView imagen;
    private Button btnVerAlertas;
    private Button btnCerrarAlerta;
    private String TAG = "Mostrar Alerta";
    Ringtone sonido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_mostrar_alerta);

        String mensajeRecibido = getIntent().getStringExtra("mensaje");

        mostrarActividadDispositivoBloqueado();

        // Instanciar vistas
        mensaje = (TextView) findViewById(R.id.tv_mensaje_alerta);
        fondo = (ConstraintLayout) findViewById(R.id.fondo_alerta);
        imagen = (ImageView) findViewById(R.id.iv_mostrar_imagen_alerta);
        btnCerrarAlerta = (Button) findViewById(R.id.btn_cerrar_alerta);
        btnVerAlertas = (Button) findViewById(R.id.btn_ver_alertas);

        // Obtener extras del inten
        String texto = getIntent().getStringExtra("texto");
        int color = getIntent().getIntExtra("color", 0);
        String rutaImagenAlerta = getIntent().getStringExtra("imagen");

        // Establece el color de fondo seleccionado
        fondo.setBackgroundColor(color);

        //Establece el texto de la alerta
        if (texto.equals("")) {
            switch (mensajeRecibido){
                case "C":
                    texto = "Negativo";
                    break;
                case "E":
                    texto = "Beber";
                    break;
                case "G":
                    texto = "Baño";
                    break;
                default:
                    texto = "Afirmativo";
                    break;
            }
        }
        mensaje.setText(texto);

        // Establece la imagen para mostrar
        if (rutaImagenAlerta.equals("")) {
            TypedArray arrPictogramas = getResources().obtainTypedArray(R.array.array_pictogramas);
            Drawable d;
            switch (mensajeRecibido){
                case "C":
                    d = arrPictogramas.getDrawable(1);
                    break;
                case "E":
                    d = arrPictogramas.getDrawable(2);
                    break;
                case "G":
                    d = arrPictogramas.getDrawable(3);
                    break;
                default:
                    d = arrPictogramas.getDrawable(0);
                    break;
            }
            Glide.with(this).load(d).fitCenter().into(imagen);
        } else {
            Glide.with(this).load(rutaImagenAlerta).fitCenter().into(imagen);
        }

        // Establece el sonido
        String rutaSonido = getIntent().getStringExtra("sonido");
        Uri uriSonido;
        if (rutaSonido.equals("")) {
            uriSonido = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_ALARM);
        } else {
            uriSonido = Uri.parse(rutaSonido);
        }

        sonido = RingtoneManager.getRingtone(getApplicationContext(), uriSonido);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_cerrar_alerta:
                        finish();
                        break;
                    case R.id.btn_ver_alertas:
                        Intent i= new Intent(getApplicationContext(), MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(i);
                        break;


                }
            }
        };
        btnCerrarAlerta.setOnClickListener(listener);
        btnVerAlertas.setOnClickListener(listener);



    }

    /**
     * Muestra la actividad aunque el dispositivo esté bloqueado o se esté realizando otra tarea.
     */
    private void mostrarActividadDispositivoBloqueado() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON ;//| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            getWindow().setAttributes(params);

//            WindowInsetsController insetsController = getWindow().getInsetsController();
//            if (insetsController != null) {
//                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
//                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
//            }
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    + WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    + WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    + WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    + WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                    + WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    + WindowManager.LayoutParams.FLAG_SECURE);
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
        sonido.stop();
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
        sonido.play();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onPause() {
        sonido.stop();
        super.onPause();
        Log.d(TAG, "onPause");
    }
}