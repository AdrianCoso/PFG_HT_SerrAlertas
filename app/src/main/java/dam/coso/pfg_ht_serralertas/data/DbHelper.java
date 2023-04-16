package dam.coso.pfg_ht_serralertas.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "SerrAlertas.db";
    public static final String TABLE_ALERTAS = "Alertas";
    public static final String CAMPO_ID_ALERTA = "id_alerta";
    public static final String TABLE_PERFILES = "Perfiles";
    public static final String CAMPO_TEXTO = "texto";
    public static final String CAMPO_COLOR = "color";
    public static final String CAMPO_IMAGEN = "rutaImagen";
    public static final String CAMPO_AUDIO = "rutaAudio";
    public static final String CAMPO_ACTIVA = "activa";
    public static final String CAMPO_ID_PERFIL = "id_perfil";
    public static final String CAMPO_NOMBRE = "nombre";
    public static final String CAMPO_ALERTA_1 = "A";
    public static final String CAMPO_ALERTA_2 = "C";
    public static final String CAMPO_ALERTA_3 = "E";
    public static final String CAMPO_ALERTA_4 = "G";
    private static DbHelper sInstance;

    public DbHelper(Context context, String databaseName, Object o, int databaseVersion) {
        super(context, databaseName, null, databaseVersion);
    }


    public static synchronized DbHelper getInstance(Context context)
    {
        if (sInstance == null) {
            sInstance = new DbHelper(context.getApplicationContext(),DATABASE_NAME,null,DATABASE_VERSION);
        }
        return sInstance;
    }

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Activar claves foráneas
        sqLiteDatabase.execSQL("PRAGMA foreign_keys = ON");

        // Crear tabla Alertas
        sqLiteDatabase.execSQL("CREATE TABLE "+TABLE_ALERTAS + "("+
                CAMPO_ID_ALERTA + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                CAMPO_TEXTO + " TEXT NOT NULL,"+
                CAMPO_COLOR + " INTEGER NOT NULL,"+
                CAMPO_IMAGEN + " TEXT NOT NULL,"+
                CAMPO_AUDIO + " TEXT NOT NULL,"+
                CAMPO_ACTIVA + " INTEGER NOT NULL)");

        // Crear tabla perfiles. A cada perfil le asignamos cuatro alertas.
        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PERFILES + "(" +
                CAMPO_ID_PERFIL + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CAMPO_NOMBRE + " TEXT NOT NULL," +
                CAMPO_ALERTA_1 + " INTEGER NOT NULL," +
                CAMPO_ALERTA_2 + " INTEGER NOT NULL," +
                CAMPO_ALERTA_3 + " INTEGER NOT NULL," +
                CAMPO_ALERTA_4 + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + CAMPO_ALERTA_1 + ") REFERENCES " + TABLE_ALERTAS + "(" + CAMPO_ID_ALERTA + ")," +
                "FOREIGN KEY (" + CAMPO_ALERTA_2 + ") REFERENCES " + TABLE_ALERTAS + "(" + CAMPO_ID_ALERTA + ")," +
                "FOREIGN KEY (" + CAMPO_ALERTA_3 + ") REFERENCES " + TABLE_ALERTAS + "(" + CAMPO_ID_ALERTA + ")," +
                "FOREIGN KEY (" + CAMPO_ALERTA_4 + ") REFERENCES " + TABLE_ALERTAS + "(" + CAMPO_ID_ALERTA + "))");


    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE "+TABLE_ALERTAS);
        sqLiteDatabase.execSQL("DROP TABLE "+TABLE_PERFILES);
        onCreate(sqLiteDatabase);
    }

}