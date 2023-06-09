package dam.coso.pfg_ht_serralertas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Set;

import dam.coso.pfg_ht_serralertas.adapters.BtSpinnerAdapter;
import dam.coso.pfg_ht_serralertas.entidades.DispositivoBluetooth;
import dam.coso.pfg_ht_serralertas.servicios.BtService;

public class BtConfigActivity extends AppCompatActivity {
    String direccionMACSeleccionada;
    private String TAG = "BtConfigActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_bt_config);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Set<BluetoothDevice> deviceSet = bluetoothAdapter.getBondedDevices();
        ArrayList<DispositivoBluetooth> listaDispositivos = new ArrayList<>();
        for (BluetoothDevice device :
                deviceSet) {
            listaDispositivos.add(new DispositivoBluetooth(device.getName(), device.getAddress()));
        }
        BtSpinnerAdapter adapter = new BtSpinnerAdapter(listaDispositivos);

        Spinner spinner = (Spinner) findViewById(R.id.sp_dispositivos_bt);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                direccionMACSeleccionada = listaDispositivos.get(position).getDireccionMAC();
                getSharedPreferences("DATOS", Context.MODE_PRIVATE).edit().putString("direccionMAC", direccionMACSeleccionada).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btnConectarBt = (Button) findViewById(R.id.btn_conectar_bt);
        btnConectarBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentServicioBt = new Intent(getApplicationContext(), BtService.class);

                stopService(intentServicioBt);
                startService(intentServicioBt);
            }
        });

        Button btnDesconectarBt = (Button) findViewById(R.id.btn_desconectar_bt);
        btnDesconectarBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().getSharedPreferences("DATOS", MODE_PRIVATE).edit().putBoolean("btConectado", false).apply();
                Intent intentServicioBt = new Intent(getApplicationContext(), BtService.class);
                stopService(intentServicioBt);
            }
        });
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