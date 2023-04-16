package dam.coso.pfg_ht_serralertas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import dam.coso.pfg_ht_serralertas.R;
import dam.coso.pfg_ht_serralertas.entidades.Perfil;

public class PerfilSpinnerAdapter extends BaseAdapter {

    private ArrayList<Perfil> perfiles;

    public PerfilSpinnerAdapter(ArrayList<Perfil> lista) {
        perfiles = lista;
    }

    @Override
    public int getCount() {
        return perfiles.size();
    }

    @Override
    public Perfil getItem(int position) {
        return perfiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return perfiles.get(position).getIdPerfil();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_layout, parent, false);

        }
        TextView idPerfil = (TextView) view.findViewById(R.id.tv_id_perfil);
        idPerfil.setText(String.valueOf(getItem(position).getIdPerfil()));

        TextView nombrePerfil = (TextView) view.findViewById(R.id.tv_nombre_perfil);
        nombrePerfil.setText(getItem(position).getNombre());

        return view;
    }
}
