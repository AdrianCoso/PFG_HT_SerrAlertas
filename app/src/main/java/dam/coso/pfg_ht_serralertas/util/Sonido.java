package dam.coso.pfg_ht_serralertas.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class Sonido {
    private static MediaPlayer mediaPlayer;

    public static void reproducir(String rutaSonido) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        File file = new File(Environment.getExternalStorageDirectory(),rutaSonido);
        if(!file.exists()){ // Si no existe, crea el archivo.
            //Toast.makeText(context,"No existe el fichero",Toast.LENGTH_SHORT);
            Log.v("AUDIO", "no existe el fichero: "+ rutaSonido);
        }
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(rutaSonido);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public static String getTitulo(String rutaSonido, ContentResolver musicResolver) {

        //retrieve song info
        //ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null, null, null, null);
        //iterate over results if valid
        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int data = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisdata = musicCursor.getString(data);
                if (thisdata.equals(rutaSonido)) {
                    return thisTitle;
                }
            }
            while (musicCursor.moveToNext());
        }
        return "Sin título";

    }
}
