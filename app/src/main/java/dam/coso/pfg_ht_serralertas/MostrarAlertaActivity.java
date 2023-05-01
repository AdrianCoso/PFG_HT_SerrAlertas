package dam.coso.pfg_ht_serralertas;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.TextView;

public class MostrarAlertaActivity extends AppCompatActivity {
    private TextView mensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_alerta);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            params.flags |= WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            getWindow().setAttributes(params);

            WindowInsetsController insetsController = getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
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

        mensaje = (TextView) findViewById(R.id.tv_mensaje_alerta);
        String mensajeRecibido = getIntent().getStringExtra("mensaje");
        mensaje.setText(mensajeRecibido);
    }
}