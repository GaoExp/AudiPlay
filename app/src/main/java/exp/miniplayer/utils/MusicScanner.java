package exp.miniplayer.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import exp.miniplayer.model.Audio;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MusicScanner {

    public static List<Audio> scanAllAudio(Context context) {
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
                MediaStore.Audio.Media.DATA
        };
        String selection = MediaStore.Audio.Media.DURATION + " > 0";
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
                int dataCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int sizeCol = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
                int mimeCol = cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE);

                do {
                    String filePath = dataCol >= 0 ? cursor.getString(dataCol) : null;
                    String mimeType = mimeCol >= 0 ? cursor.getString(mimeCol) : null;
                    String ext = getFileExtension(filePath);

                    if ((ext != null && isMusicExtension(ext))
                            || isMusicMimeType(mimeType)) {
                        continue;
                    }
                    boolean isAudioByMime = mimeType != null
                            && mimeType.toLowerCase().startsWith("audio/");
                    if (!isAudioByMime && ext == null) {
                        continue;
                    }

                    long id = idCol >= 0 ? cursor.getLong(idCol) : 0;
                    String title = titleCol >= 0 ? cursor.getString(titleCol) : null;
                    String artist = artistCol >= 0 ? cursor.getString(artistCol) : null;
                    String album = albumCol >= 0 ? cursor.getString(albumCol) : null;
                    long duration = durationCol >= 0 ? cursor.getLong(durationCol) : 0;
                    long albumId = albumIdCol >= 0 ? cursor.getLong(albumIdCol) : 0;
                    long dateAdded = dateAddedCol >= 0 ? cursor.getLong(dateAddedCol) : 0;
                    long fileSize = sizeCol >= 0 ? cursor.getLong(sizeCol) : 0;

                    String contentUri = Uri.withAppendedPath(
                            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(id)).toString();

                    String albumArt = null;
                    if (albumId > 0) {
                        albumArt = Uri.parse("content://media/external/audio/albumart/" + albumId).toString();
                    }

                    Audio audio = new Audio(id,
                            title != null ? title : "",
                            artist != null ? artist : "",
                            album != null ? album : "",
                            duration, contentUri, albumArt, dateAdded);
                    audio.setFilePath(filePath);
                    audio.setFileSize(fileSize);
                    audioList.add(audio);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return audioList;
    }

    public static List<Audio> scanAudio(Context context) {
        return scanAudio(context, null);
    }

    public static List<Audio> scanAudio(Context context, PreferencesManager prefs) {
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
                MediaStore.Audio.Media.IS_MUSIC,
                MediaStore.Audio.Media.DATA
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
                int dataCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                boolean limitFolders = prefs != null && prefs.isLimitFolders();
                Set<String> included = limitFolders ? prefs.getIncludedFolders() : null;
                Set<String> excluded = prefs != null ? prefs.getExcludedFolders() : null;
                boolean hasIncluded = included != null && !included.isEmpty();
                boolean hasExcluded = excluded != null && !excluded.isEmpty();

                do {
                    String filePath = dataCol >= 0 ? cursor.getString(dataCol) : null;

                    if (hasIncluded && filePath != null) {
                        boolean match = false;
                        for (String folder : included) {
                            if (filePath.startsWith(folder)) {
                                match = true;
                                break;
                            }
                        }
                        if (!match) continue;
                    }

                    if (hasExcluded && filePath != null) {
                        boolean match = false;
                        for (String folder : excluded) {
                            if (filePath.startsWith(folder)) {
                                match = true;
                                break;
                            }
                        }
                        if (match) continue;
                    }

                    Set<String> audioFormats = prefs != null ? prefs.getAudioFormats() : null;
                    if (audioFormats != null && !audioFormats.isEmpty() && filePath != null) {
                        String ext = getFileExtension(filePath);
                        if (ext != null && !audioFormats.contains(ext)) continue;
                    }

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

                    int sizeCol = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
                    long fileSize = sizeCol >= 0 ? cursor.getLong(sizeCol) : 0;

                    Audio audio = new Audio(id,
                            title != null ? title : "",
                            artist != null ? artist : "",
                            album != null ? album : "",
                            duration,
                            contentUri,
                            albumArt,
                            dateAdded);
                    audio.setFilePath(filePath);
                    audio.setFileSize(fileSize);
                    audioList.add(audio);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return audioList;
    }

    private static String getFileExtension(String path) {
        if (path == null) return null;
        int dot = path.lastIndexOf('.');
        if (dot >= 0 && dot < path.length() - 1) {
            return path.substring(dot + 1).toLowerCase();
        }
        return null;
    }

    private static boolean isMusicExtension(String ext) {
        return "mp3".equals(ext) || "m4a".equals(ext) || "mp4".equals(ext);
    }

    private static boolean isMusicMimeType(String mime) {
        if (mime == null) return false;
        String m = mime.toLowerCase();
        return "audio/mpeg".equals(m) || "audio/mp3".equals(m)
                || "audio/x-m4a".equals(m) || "audio/mp4".equals(m)
                || "audio/aac".equals(m) || "audio/acc".equals(m);
    }
}
