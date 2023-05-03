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

    Context context;
    public DbAlertas(@Nullable Context context) {
        super(context);
        this.context = context;
    }

    /**
     * Inserta una alerta genérica según el número de botón al que queda asociada.
     * @param i
     * @return La id generada para la la alerta insertada. 0 si ha habido un error con la base de
     * datos, -1 si ha fallado la inserción
     */
    public long insertarAlerta(int i){
        long id = 0;

        DbHelper dbHelper =  DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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
        } finally {
            db.close();
            dbHelper.close();
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
            DbHelper dbHelper = DbHelper.getInstance(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(CAMPO_NOMBRE,nombrePerfil);
            values.put(CAMPO_ALERTA_1, ids[0]);
            values.put(CAMPO_ALERTA_2, ids[1]);
            values.put(CAMPO_ALERTA_3, ids[2]);
            values.put(CAMPO_ALERTA_4, ids[3]);
            id = db.insert(TABLE_PERFILES, null, values);
            db.close();
            dbHelper.close();
        } catch (Exception ex) {
            ex.toString();
        }

        return id;
    }

    public boolean eliminarPefil(long id) {
        boolean correcto = false;

        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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

            db.delete(TABLE_PERFILES, CAMPO_ID_PERFIL+"=?", idPerfil);

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


            db.delete(TABLE_ALERTAS, where, alarmas);

            db.close();
            dbHelper.close();
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
        DbHelper dbHelper =  DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

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
            db.close();
            dbHelper.close();
        }

    }

//    public Botones mostrarBoton(int id){
//        DbHelper dbHelper =  new DbHelper(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        Botones boton = new Botones();
//        Cursor cursorBotones = null;
//        cursorBotones = db.rawQuery("SELECT * FROM " + TABLE_ALERTAS + " WHERE id_boton="+ id, null);
//        if (cursorBotones.moveToFirst()){
//            boton.setId(cursorBotones.getInt(0));
//            boton.setNumero(cursorBotones.getInt(1));
//            boton.setTexto(cursorBotones.getString(2));
//            boton.setColor(cursorBotones.getInt(3));
//            boton.setImagen(cursorBotones.getString(4));
//            boton.setAudio(cursorBotones.getString(5));
//            boton.setActivado(cursorBotones.getString(6));
//
//        }
//        cursorBotones.close();
//        return boton;
//    }

    public Alerta mostrarAlerta(int id) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

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

        db.close();
        dbHelper.close();
        return alerta;

    }

    /**
     * Modifica los campos de un registro de la tabla alertas
     * @param id La id de la alerta que modificamos
     * @param texto El nuevo texto para la alerta
     * @param color El nuevo color para la alerta
     * @param imagen La nueva ruta de imagen para la alerta
     * @param audio La nueva ruta de audio para la alerta
     * @return El número de registros afectados. -1 Si ha habido algún problema con la base de datos.
     */
    public int editarAlerta(int id, String texto, int color, String imagen, String audio) {

        DbHelper dbHelper =  DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int reg = -1;
        ContentValues cv = new ContentValues();
        cv.put(CAMPO_TEXTO, texto);
        cv.put(CAMPO_COLOR, color);
        cv.put(CAMPO_IMAGEN, imagen);
        cv.put(CAMPO_AUDIO, audio);
        String where = new StringBuilder().append(CAMPO_ID_ALERTA).append("=?").toString();
        String[] arg = new String[]{String.valueOf(id)};

        try{
            reg = db.update(TABLE_ALERTAS, cv, where, arg );

        } catch (Exception ex){
            ex.toString();

        } finally {
            db.close();
            db.close();
        }

        return reg;
    }

    public boolean switchBoton(int id, boolean activado){
        boolean correcto = false;

        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try{
            if (activado) {
                db.execSQL("UPDATE "+ TABLE_ALERTAS+" SET activado = 'activado' WHERE id_boton = "+id);
            } else {
                db.execSQL("UPDATE "+ TABLE_ALERTAS+" SET activado = 'desactivado' WHERE id_boton = "+id);
            }
            correcto = true;
        } catch (Exception ex){
            ex.toString();
            correcto = false;
        } finally {
            db.close();
            dbHelper.close();
        }

        return correcto;
    }

    public void cargarPerfilesALista(ArrayList<Perfil> listaPerfiles) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

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

        }
        db.close();
        dbHelper.close();
    }

    public int modificarAlerta(Alerta alerta) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
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
        db.close();
        dbHelper.close();
        return i;
    }

    /**
     * Devuelve la alerta correspondiente al botón pulsado para un perfil determinado
     *
     * @param idPerfilActivo la id del perfil correspondiente
     * @param mensajeRecibido el mesaje recibido por el dispositivo bt. Corresponde al nombre del campo en el que guardamos la id de la alerta
     * @return
     */
    public Alerta mostrarAlertaRecibida(int idPerfilActivo, String mensajeRecibido) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

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

//    public Botones obtenerBotonPorMensaje(String mensaje) {
//        // Encontrar el número que corresponde el botón según el mensaje que hayamos recibido usando el array de mensajes que se pueden recibir
//        Integer numeroBoton = MensajesBotonera.mapaBotones.get(mensaje);
//
//        DbHelper dbHelper =  new DbHelper(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        Botones boton = new Botones();
//        Cursor cursorBotones = null;
//        cursorBotones = db.rawQuery("SELECT * FROM " + TABLE_ALERTAS + " WHERE numero="+ String.valueOf(numeroBoton) +" AND activado = 'activado'", null);
//        if (cursorBotones.moveToFirst()){
//            boton.setId_boton(cursorBotones.getInt(0));
//            boton.setNumero(cursorBotones.getInt(1));
//            boton.setTexto(cursorBotones.getString(2));
//            boton.setColor(cursorBotones.getInt(3));
//            boton.setImagen(cursorBotones.getString(4));
//            boton.setAudio(cursorBotones.getString(5));
//
//        } else {
//            return null;
//        }
//        cursorBotones.close();
//        return boton;
//
//    }

}
