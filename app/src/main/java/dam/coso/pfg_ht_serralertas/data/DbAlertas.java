package dam.coso.pfg_ht_serralertas.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import dam.coso.pfg_ht_serralertas.entidades.Perfil;

//import com.example.portada.MensajesBotonera;
//import com.example.portada.entidades.Botones;

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
        try{
            DbHelper dbHelper =  DbHelper.getInstance(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String texto = new StringBuilder().append("Botón ").append(i).toString();
            int color = i * 100;
            ContentValues values = new ContentValues();
            values.put(CAMPO_TEXTO,texto);
            values.put(CAMPO_COLOR,color);
            values.put(CAMPO_IMAGEN,"");
            values.put(CAMPO_AUDIO,"");
            values.put(CAMPO_ACTIVA, 1);

            id = db.insert(TABLE_ALERTAS, null, values);

            db.close();
            dbHelper.close();
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
                ids[i] = insertarAlerta(i+1);
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
        } catch (Exception ex) {
            ex.toString();
        }

        return id;
    }

    public boolean eliminarPefil(int id) {
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

        }
        return correcto;
    }

    /**
     * Selecciona las alertas que corresponden a un perfil determinado
     * @param idPerfil id del perfil cuyas alertas queremos seleccionar
     * @return Un Cursor conteniendo los registros de las alertas.
     */
    public Cursor mostrarAlertasPorPerfil(long idPerfil){
        DbHelper dbHelper =  DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Consulta para obtener las alarmas correspondientes al perfil
        String sql =
                "SELECT a.* " +
                "FROM "+TABLE_ALERTAS+" a " +
                "JOIN "+TABLE_PERFILES+" p ON a.id_alerta = p.A OR a.id_alerta = p.C OR a.id_alerta = p.E OR a.id_alerta = p.G " +
                "WHERE p.id_perfil = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(idPerfil)});
        return cursor;
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
        }

        return correcto;
    }

    public Cursor mostrarPerfiles() {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(TABLE_PERFILES, null, null, null, null, null, null);
        return cursor;

    }

    public void cargarNombresPerfilesALista(ArrayList<String> nombresPerfiles) {
        DbHelper dbHelper = DbHelper.getInstance(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(TABLE_PERFILES, new String[] {CAMPO_NOMBRE}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            nombresPerfiles.clear();
            do {
                nombresPerfiles.add(cursor.getString(0));
            } while (cursor.moveToNext());

        }
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
