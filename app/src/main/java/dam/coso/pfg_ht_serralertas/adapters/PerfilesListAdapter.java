package dam.coso.pfg_ht_serralertas.adapters;

import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;

import dam.coso.pfg_ht_serralertas.R;
import dam.coso.pfg_ht_serralertas.data.DbAlertas;
import dam.coso.pfg_ht_serralertas.entidades.Perfil;

public class PerfilesListAdapter extends BaseAdapter {
    ArrayList<Perfil> listaPerfiles;

    public PerfilesListAdapter(ArrayList<Perfil> listaPerfiles) {
        this.listaPerfiles = listaPerfiles;
    }

    @Override
    public int getCount() {
        return listaPerfiles.size();
    }

    @Override
    public Perfil getItem(int position) {
        return listaPerfiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaPerfiles.get(position).getIdPerfil();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.item_perfil, parent, false);

        TextView tvNombrePerfilLista = (TextView) view.findViewById(R.id.tv_nombre_perfil_lista);
        final String nombrePerfil = listaPerfiles.get(position).getNombre();
        tvNombrePerfilLista.setText(nombrePerfil);

        ImageButton ibBorrarPerfil = (ImageButton) view.findViewById(R.id.ib_borrar_perfil);
        ibBorrarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Eliminar perfil "+nombrePerfil)
                        .setMessage("Se perderán todas las alertas asociadas al perfil. ¿Está seguro?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {

                            long id = PerfilesListAdapter.this.getItemId(position);
                            DbAlertas db = new DbAlertas(view.getContext());
                            try {
                                db.eliminarPefil(id);
                                db.cargarPerfilesALista(listaPerfiles);
                                notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                db.cerrarBD();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();

            }
        });

        return view;
    }
}
