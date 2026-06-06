package exp.miniplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import exp.miniplayer.model.Audio;

import java.util.ArrayList;
import java.util.List;

public class MusicScanner {

    public static List<Audio> scanAudio(Context context) {
        List<Audio> audioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.IS_MUSIC
        };
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 AND "
                + MediaStore.Audio.Media.DURATION + " > 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";

        try (Cursor cursor = context.getContentResolver().query(
                uri, projection, selection, null, sortOrder)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idCol = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int titleCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int durationCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                int albumIdCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int dateAddedCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED);

                do {
                    long id = idCol >= 0 ? cursor.getLong(idCol) : 0;
                    String title = titleCol >= 0 ? cursor.getString(titleCol) : null;
                    String artist = artistCol >= 0 ? cursor.getString(artistCol) : null;
                    String album = albumCol >= 0 ? cursor.getString(albumCol) : null;
                    long duration = durationCol >= 0 ? cursor.getLong(durationCol) : 0;
                    long albumId = albumIdCol >= 0 ? cursor.getLong(albumIdCol) : 0;
                    long dateAdded = dateAddedCol >= 0 ? cursor.getLong(dateAddedCol) : 0;

                    String contentUri = Uri.withAppendedPath(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(id)).toString();

                    String albumArt = null;
                    if (albumId > 0) {
                        albumArt = Uri.parse("content://media/external/audio/albumart/" + albumId).toString();
                    }

                    Audio audio = new Audio(id,
                            title != null ? title : "Unknown Title",
                            artist != null ? artist : "Unknown Artist",
                            album != null ? album : "Unknown Album",
                            duration,
                            contentUri,
                            albumArt,
                            dateAdded);
                    audioList.add(audio);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return audioList;
    }
}
