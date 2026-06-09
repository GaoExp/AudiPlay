package exp.miniplayer.utils;

import android.content.Context;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.database.PlaylistEntity;
import exp.miniplayer.database.PlaylistSongEntity;

public class PlaylistIO {

    public static void exportAsM3U(Context context, AudioRepository repo,
                                   PlaylistEntity playlist, OutputStream output) throws Exception {
        List<PlaylistSongEntity> songs = repo.getPlaylistSongsSync(playlist.getId());
        OutputStreamWriter writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);

        writer.write("#EXTM3U\n");
        writer.write("#PLAYLIST:" + playlist.getName() + "\n");
        for (PlaylistSongEntity song : songs) {
            String duration = String.valueOf(song.getDuration() / 1000);
            String display = song.getArtist() + " - " + song.getTitle();
            writer.write("#EXTINF:" + duration + "," + display + "\n");
            writer.write(song.getUri() + "\n");
        }
        writer.flush();
    }

    public static void exportAllAsJSON(Context context, AudioRepository repo,
                                       OutputStream output) throws Exception {
        List<PlaylistEntity> playlists = repo.getPlaylistsSync();
        JSONArray root = new JSONArray();

        for (PlaylistEntity playlist : playlists) {
            JSONObject pObj = new JSONObject();
            pObj.put("name", playlist.getName());
            pObj.put("createdAt", playlist.getCreatedAt());

            JSONArray songsArr = new JSONArray();
            List<PlaylistSongEntity> songs = repo.getPlaylistSongsSync(playlist.getId());
            for (PlaylistSongEntity song : songs) {
                JSONObject sObj = new JSONObject();
                sObj.put("audioId", song.getAudioId());
                sObj.put("title", song.getTitle());
                sObj.put("artist", song.getArtist());
                sObj.put("album", song.getAlbum());
                sObj.put("duration", song.getDuration());
                sObj.put("uri", song.getUri());
                sObj.put("albumArt", song.getAlbumArt() != null ? song.getAlbumArt() : "");
                sObj.put("sortOrder", song.getSortOrder());
                sObj.put("addedAt", song.getAddedAt());
                songsArr.put(sObj);
            }
            pObj.put("songs", songsArr);
            root.put(pObj);
        }

        OutputStreamWriter writer = new OutputStreamWriter(output, StandardCharsets.UTF_8);
        writer.write(root.toString(2));
        writer.flush();
    }

    public static void importM3U(Context context, AudioRepository repo,
                                 Uri fileUri, String playlistName) throws Exception {
        StringBuilder content = new StringBuilder();
        try (InputStream is = context.getContentResolver().openInputStream(fileUri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        String[] lines = content.toString().split("\n");
        String name = playlistName;
        for (String line : lines) {
            if (line.startsWith("#PLAYLIST:")) {
                String parsedName = line.substring(10).trim();
                if (!parsedName.isEmpty()) name = parsedName;
                break;
            }
        }
        if (name == null) name = "Imported M3U";
        long playlistId = repo.createPlaylist(name);

        String currentTitle = null;
        String currentArtist = null;
        long currentDuration = 0;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#EXTM3U") || line.startsWith("#PLAYLIST:")) {
                continue;
            }
            if (line.startsWith("#EXTINF:")) {
                String meta = line.substring(8);
                String[] parts = meta.split(",", 2);
                try {
                    currentDuration = Long.parseLong(parts[0]) * 1000;
                } catch (Exception ignored) {
                    currentDuration = 0;
                }
                if (parts.length > 1) {
                    String[] nameParts = parts[1].split(" - ", 2);
                    if (nameParts.length > 1) {
                        currentArtist = nameParts[0].trim();
                        currentTitle = nameParts[1].trim();
                    } else {
                        currentTitle = nameParts[0].trim();
                        currentArtist = "";
                    }
                }
                continue;
            }
            String uri = line;
            String title = currentTitle != null ? currentTitle : "Unknown";
            String artist = currentArtist != null ? currentArtist : "Unknown";
            long sortOrder = repo.getPlaylistSongCount((int) playlistId);
            PlaylistSongEntity entity = new PlaylistSongEntity(
                    (int) playlistId, 0, title, artist, "",
                    currentDuration, uri, null, (int) sortOrder, System.currentTimeMillis());
            repo.getPlaylistSongDao().insert(entity);
            currentTitle = null;
            currentArtist = null;
            currentDuration = 0;
        }
    }

    public static void importJSON(Context context, AudioRepository repo,
                                  Uri fileUri) throws Exception {
        StringBuilder content = new StringBuilder();
        try (InputStream is = context.getContentResolver().openInputStream(fileUri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        JSONArray root = new JSONArray(content.toString());
        for (int i = 0; i < root.length(); i++) {
            JSONObject pObj = root.getJSONObject(i);
            String name = pObj.getString("name");
            long createdAt = pObj.optLong("createdAt", System.currentTimeMillis());
            long playlistId = repo.createPlaylist(name + " (imported)");

            JSONArray songsArr = pObj.getJSONArray("songs");
            for (int j = 0; j < songsArr.length(); j++) {
                JSONObject sObj = songsArr.getJSONObject(j);
                long audioId = sObj.optLong("audioId", 0);
                String title = sObj.optString("title", "Unknown");
                String artist = sObj.optString("artist", "Unknown");
                String album = sObj.optString("album", "");
                long duration = sObj.optLong("duration", 0);
                String uri = sObj.optString("uri", "");
                String albumArt = sObj.optString("albumArt", null);
                int sortOrder = sObj.optInt("sortOrder", j);
                long addedAt = sObj.optLong("addedAt", System.currentTimeMillis());

                if (albumArt != null && albumArt.isEmpty()) albumArt = null;
                PlaylistSongEntity entity = new PlaylistSongEntity(
                        (int) playlistId, audioId, title, artist, album,
                        duration, uri, albumArt, sortOrder, addedAt);
                repo.getPlaylistSongDao().insert(entity);
            }
        }
    }
}
