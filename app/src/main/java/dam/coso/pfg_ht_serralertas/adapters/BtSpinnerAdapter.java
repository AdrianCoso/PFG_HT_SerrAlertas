package dam.coso.pfg_ht_serralertas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dam.coso.pfg_ht_serralertas.R;
import dam.coso.pfg_ht_serralertas.entidades.DispositivoBluetooth;

public class BtSpinnerAdapter extends BaseAdapter {
    ArrayList<DispositivoBluetooth> datos;

    public BtSpinnerAdapter(ArrayList<DispositivoBluetooth> datos) {
        this.datos = datos;
    }

    @Override
    public int getCount() {
        return datos.size();
    }

    @Override
    public DispositivoBluetooth getItem(int position) {
        return datos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.elemento_spinner_bt, parent, false);

        }
        TextView tvNombreDispositivo = (TextView) view.findViewById(R.id.tv_nombre_dispositivo);
        tvNombreDispositivo.setText(String.valueOf(getItem(position).getNombreDispositivoBt()));

        TextView tvDireccionMAC = (TextView) view.findViewById(R.id.tv_direccion_MAC);
        tvDireccionMAC.setText(getItem(position).getDireccionMAC());

        return view;
    }
}
