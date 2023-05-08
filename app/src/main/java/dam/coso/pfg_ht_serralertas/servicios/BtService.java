package dam.coso.pfg_ht_serralertas.servicios;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import dam.coso.pfg_ht_serralertas.MainActivity;
import dam.coso.pfg_ht_serralertas.MostrarAlertaActivity;
import dam.coso.pfg_ht_serralertas.R;
import dam.coso.pfg_ht_serralertas.hilos.HiloAlerta;

public class BtService extends Service {
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket socketBt = null;
    private HiloConexion hiloConexion;
    String TAG = "btservice";

    public BtService() {
    }

    @Override
    public void onCreate() {
        BluetoothAdapter adaptadorBt = BluetoothAdapter.getDefaultAdapter();
        comprobarEstadoBt(adaptadorBt);
        Log.d(TAG, "Iniciado servicio bt");

        // Crear el dispositivo y establecer su dirección MAC
        String direccionMAC = getSharedPreferences("DATOS", Context.MODE_PRIVATE).getString("direccionMAC", "");

        BluetoothDevice dispositivo;
        if (!direccionMAC.equals("")) {
            dispositivo = adaptadorBt.getRemoteDevice(direccionMAC);
            Log.d(TAG, "arrancando dirección MAC" + direccionMAC);
        } else {
            Log.d(TAG, "Dirección MAC nula");
            return;
        }

        // Crear el socket
        try {
            socketBt = crearSocketBluetooth(dispositivo);
            Log.d(TAG, "socket creado");
        } catch (IOException e) {
            Log.d(TAG, "Fallo al crear el socket");
            e.printStackTrace();
        }

        // Conectar al socket
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            //MainActivity.peticionPermisoBT.launch(Manifest.permission.BLUETOOTH_CONNECT);
        }
        try {
            socketBt.connect();
            if (socketBt.isConnected()) {
                Log.d(TAG, "Socket conectado");
                Toast.makeText(this, "Conectado", Toast.LENGTH_SHORT).show();
            } else return;
        } catch (IOException e) {
            Log.d(TAG, "No se pudo conectar al socket");
            e.printStackTrace();
        }

        hiloConexion = new HiloConexion(socketBt);

        // Crear una notificación que abra la portada de la aplicación al tocarla
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // crear la notificación con un canal propio
        CharSequence name = "Notificación de conexión";
        String description = "Informa de la conexión Bluetooth con la botonera";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel("mi_canal_id", name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        Notification notification = new Notification.Builder(this, "mi_canal_id")
                .setContentTitle("Escuchando el dispositivo " + dispositivo.getName())
                .setContentText("Pulsa para abrir la aplicación")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        hiloConexion.start();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (hiloConexion.isAlive()) {
            hiloConexion.cerrarConexion();

        }
        try {
            socketBt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BluetoothSocket crearSocketBluetooth(BluetoothDevice dispositivo) throws IOException {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

        }
        return dispositivo.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private void comprobarEstadoBt(BluetoothAdapter adaptadorBt) {
        if (adaptadorBt == null) {
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_SHORT).show();
        } else if (!adaptadorBt.isEnabled()) {
            Toast.makeText(getBaseContext(), "El dispositivo no está habilitado", Toast.LENGTH_SHORT).show();
            //TODO Solicitar al usuario que habilite el bluetooth


        } else {
            Log.d(TAG, "bt operativo");
        }
    }

    private class HiloConexion extends Thread {
        private final InputStream input;
        private boolean conexionAbierta;

        public HiloConexion(BluetoothSocket socket) {
            InputStream tmpInput = null;

            try {
                tmpInput = socket.getInputStream();
                Log.d(TAG, "obteniendo flujo de entrada");
            } catch (IOException e) {
                Log.d(TAG, "no es posible obtener flujo de entrada");
                e.printStackTrace();
            }
            input = tmpInput;
            conexionAbierta = true;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1];
            int numeroBytes;

            // Mantener el bucle para escuchar mensajes entrantes
            while (conexionAbierta) {
                try {
                    numeroBytes = input.read(buffer);
                    //Toast.makeText(BtService.this, "Mensaje recibido", Toast.LENGTH_SHORT).show();
                    String mensajeRecibido = new String(buffer, 0, numeroBytes);
                    Log.d(TAG, "mensaje recibido: "+mensajeRecibido);

                    // Lanzar un hilo para mostrar la actividad de alerta con el mensaje recibido
                    // si es una de las letras de pulsación
                    if (mensajeRecibido.equals("A") || mensajeRecibido.equals("C") || mensajeRecibido.equals("E") || mensajeRecibido.equals("G")) {
                        HiloAlerta hiloAlerta = new HiloAlerta(getApplicationContext(), mensajeRecibido);
                        hiloAlerta.start();

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "No se pudo leer el flujo de entrada");
                    break;
                }
            }
            // Al acabar el bucle por haber cerrado la conexión cerramos el flujo de entrada.
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Cambia el valor del campo que mantiene la ejecución dentro del bucle que recibe los datos.
         */
        public void cerrarConexion() {
            this.conexionAbierta = false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}