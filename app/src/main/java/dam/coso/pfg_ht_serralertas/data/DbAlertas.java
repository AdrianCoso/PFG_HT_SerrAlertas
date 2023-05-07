package dam.coso.pfg_ht_serralertas.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import dam.coso.pfg_ht_serralertas.entidades.Alerta;
import dam.coso.pfg_ht_serralertas.entidades.Perfil;

public class DbAlertas extends DbHelper{

    SQLiteDatabase db;
    public DbAlertas(@Nullable Context context) {
        super(context);
        DbHelper dbHelper = DbHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * Inserta una alerta genérica según el número de botón al que queda asociada.
     * @param i Determina el texto que se asocia a la alerta por defecto. 0 Afirmativo, 1 Negativo, 2 Agua, 3 Baño.
     * @return La id generada para la la alerta insertada. 0 si ha habido un error con la base de
     * datos, -1 si ha fallado la inserción.
     */
    public long insertarAlerta(int i){
        long id = 0;
        try{

            String[] textos = new String[] {"Afirmativo", "Negativo", "Agua", "Baño"};
            String texto = textos[i];

            ContentValues values = new ContentValues();
            values.put(CAMPO_TEXTO,texto);
            values.put(CAMPO_COLOR,0);
            values.put(CAMPO_IMAGEN,"");
            values.put(CAMPO_AUDIO,"");
            values.put(CAMPO_ACTIVA, 1);

            id = db.insert(TABLE_ALERTAS, null, values);

        } catch (Exception ex) {
            ex.toString();
        }

        return id;
    }

    /**
     * Añade un registro de perfil a la base de datos. Usa el método insertarAlerta para crear
     * automáticamente cuatro registros de alerta que quedarán asociados al nuevo perfil.
     * @param nombrePerfil El nombre del nuevo perfil.
     * @return La id del nuevo perfil. 0 si ha habido un error con la base de datos, -1 si ha
     * fallado la inserción.
     */
    public long insertarPerfil(String nombrePerfil) {
        long id= 0;
        try{

            // Insertamos cuatro alertas genéricas a tabla alertas y guardamos cada id;
            long[] ids = new long[4];
            for (int i = 0; i < ids.length; i++) {
                ids[i] = insertarAlerta(i);
            }

            ContentValues values = new ContentValues();
            values.put(CAMPO_NOMBRE,nombrePerfil);
            values.put(CAMPO_ALERTA_1, ids[0]);
            values.put(CAMPO_ALERTA_2, ids[1]);
            values.put(CAMPO_ALERTA_3, ids[2]);
            values.put(CAMPO_ALERTA_4, ids[3]);
            id = db.insert(TABLE_PERFILES, null, values);

        } catch (Exception ex) {
            ex.toString();
        }

        return id;
    }

    /**
     * Elimina un registro de la tabla de perfiles y todas sus alertas asociadas.
     * @param id El número de id del registro que se va a eliminar.
     * @return Booleano que indica si la operación se ha realizado correctamente.
     */
    public boolean eliminarPefil(long id) {
        boolean correcto = true;

        String[] idPerfil = new String[] {String.valueOf(id)};

        Cursor cursor = db.query(
                TABLE_PERFILES,
                new String[]{CAMPO_ALERTA_1, CAMPO_ALERTA_2, CAMPO_ALERTA_3, CAMPO_ALERTA_4},
                CAMPO_ID_PERFIL+"=?", idPerfil, null, null, null);

        if (cursor.moveToFirst()) {
            String[] alarmas = new String[4];
            for (int i = 0; i<alarmas.length; i++) {
                alarmas[i] = String.valueOf(cursor.getInt(i));
            }

            int alertasEliminadas = db.delete(TABLE_PERFILES, CAMPO_ID_PERFIL+"=?", idPerfil);

            if (alertasEliminadas != 4) correcto = false;

            // Creamos la cláusula where concatenando lo que necesitamos para borrar todas las
            // alarmas del perfil seleccionado
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i<4; i++) {
                builder.append(CAMPO_ID_ALERTA).append("=? ");
                if (i < 3) {
                    builder.append("OR ");
                }
            }
            String where = builder.toString();


            int perfilesEliminados = db.delete(TABLE_ALERTAS, where, alarmas);

            if (perfilesEliminados != 1) correcto = false;

        }
        return correcto;
    }

    /**
     * Elimina los objetos de tipo de Alerta de una lista y la vuelve a llenar con objetos que
     * corresponden a un perfil
     *
     * @param idPerfil id del perfil cuyas alertas queremos seleccionar
     * @param lista La lista que vamos a llenar con las alertas de ese perfil.
     */
    public void mostrarAlertasPorPerfil(long idPerfil, ArrayList<Alerta> lista){

        // Consulta para obtener las alarmas correspondientes al perfil
        String sql =
                "SELECT a.* " +
                "FROM "+TABLE_ALERTAS+" a " +
                "JOIN "+TABLE_PERFILES+" p ON a.id_alerta = p.A OR a.id_alerta = p.C OR a.id_alerta = p.E OR a.id_alerta = p.G " +
                "WHERE p.id_perfil = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idPerfil)});
        if (cursor.moveToFirst()) {
            lista.clear();
            do {
                Alerta alerta = new Alerta(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getInt(5)==1);
                lista.add(alerta);
            } while (cursor.moveToNext());

        }

    }

    /**
     * Para mostrar un objeto Alerta según su id.
     * @param id Número de id de la alerta que deseamos mostrar.
     * @return Alerta con los campos indicados en el registro correspondiente a la id que pasamos.
     */
    public Alerta mostrarAlerta(int id) {
        Cursor cursor = db.query(
                TABLE_ALERTAS,
                null,
                CAMPO_ID_ALERTA + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null);
        Alerta alerta = null;
        if (cursor.moveToFirst()) {
            alerta = new Alerta(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5) == 1);
        }

        return alerta;

    }

    /**
     * Este método se podría utilizar para activar o desactivar alertas
     * @param id la ida de la alerta que deseamos activar o desactivar
     * @param activado El estado actual de la alerta
     * @return
     */
//    public boolean switchBoton(int id, boolean activado){
//        boolean correcto = false;
//
//        DbHelper dbHelper = DbHelper.getInstance(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        try{
//            if (activado) {
//                db.execSQL("UPDATE "+ TABLE_ALERTAS+" SET activado = 'activado' WHERE id_boton = "+id);
//            } else {
//                db.execSQL("UPDATE "+ TABLE_ALERTAS+" SET activado = 'desactivado' WHERE id_boton = "+id);
//            }
//            correcto = true;
//        } catch (Exception ex){
//            ex.toString();
//            correcto = false;
//        } finally {
//            db.close();
//            dbHelper.close();
//        }
//
//        return correcto;
//    }

    /**
     * Vacía una lista con instancias de clase Perfil y la rellena con las que corresponden a los registros de la tabla
     * Si por algún motivo la tabla está vacía insserta un registro por defecto y lo pone en la lista.
     * @param listaPerfiles Lista que contendrá las instancias.
     */
    public void cargarPerfilesALista(ArrayList<Perfil> listaPerfiles) {
        Cursor cursor = db.query(TABLE_PERFILES, new String[] {CAMPO_ID_PERFIL, CAMPO_NOMBRE}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            listaPerfiles.clear();
            do {
                Perfil perfil = new Perfil(
                        cursor.getInt(0),
                        cursor.getString(1)
                );
                listaPerfiles.add(perfil);
            } while (cursor.moveToNext());

        } else {
            insertarPerfil("Perfil por defecto");
            cargarPerfilesALista(listaPerfiles);
        }

    }

    /**
     * Modifica los campos de un registro de alerta utilizando, por ejemplo un objeto obtenido con
     * mostrarAlerta. Una vez utilizados los setters de la clase Alerta podemos modificar el registro usando la misma instancia como parámetro.
     * @param alerta objeto que se desea modificar
     * @return El número de registros modificados.
     */
    public int modificarAlerta(Alerta alerta) {
        ContentValues values = new ContentValues();
        values.put(CAMPO_TEXTO, alerta.getTexto());
        values.put(CAMPO_COLOR, alerta.getColor());
        values.put(CAMPO_AUDIO, alerta.getRutaSonido());
        values.put(CAMPO_IMAGEN, alerta.getRutaImagen());

        int i = db.update(
                TABLE_ALERTAS,
                values,
                CAMPO_ID_ALERTA + "=?",
                new String[]{String.valueOf(alerta.getId())}

        );
        return i;
    }

    /**
     * Devuelve la alerta correspondiente al botón pulsado para un perfil determinado.
     * Este método funciona gracias a que hemos llamado a los campos que contienen las ids de las alertas igual que los mensajes que envía el dispositivo.
     *
     * @param idPerfilActivo la id del perfil correspondiente
     * @param mensajeRecibido el mesaje recibido por el dispositivo bt. Corresponde al nombre del campo en el que guardamos la id de la alerta
     * @return
     */
    public Alerta mostrarAlertaRecibida(int idPerfilActivo, String mensajeRecibido) {
        String sql =
                "SELECT a.* " +
                        "FROM "+TABLE_ALERTAS+" a " +
                        "JOIN "+TABLE_PERFILES+" p ON a.id_alerta = p." + mensajeRecibido +
                        " WHERE p.id_perfil = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idPerfilActivo)});

        if (cursor.moveToFirst()) {
            Alerta alerta = new Alerta(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5)==1
            );
            return alerta;
        } else {
            return null;
        }
    }

    public void cerrarBD(){
        this.close();
        db.close();
    }


}
