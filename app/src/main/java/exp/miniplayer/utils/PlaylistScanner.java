package exp.miniplayer.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.database.PlaylistEntity;
import exp.miniplayer.database.PlaylistSongEntity;
import exp.miniplayer.model.Audio;

public class PlaylistScanner {

    private static final String[] PLAYLIST_EXTENSIONS = {".m3u", ".m3u8"};
    private static final int MAX_DEPTH = 8;

    public static void scanPlaylists(Context context, AudioRepository repo,
                                      List<Audio> scannedAudio) {
        Set<String> scanDirs = resolveScanDirs(context);
        Set<String> excluded = new PreferencesManager(context).getExcludedFolders();
        if (excluded == null) excluded = new HashSet<>();

        List<File> playlistFiles = new ArrayList<>();
        for (String dir : scanDirs) {
            File folder = new File(dir);
            if (folder.exists() && folder.isDirectory()) {
                collectPlaylistFiles(folder, playlistFiles, excluded, 0);
            }
        }

        for (File file : playlistFiles) {
            importPlaylistFile(context, repo, file, scannedAudio);
        }
    }

    public static Set<String> resolveScanDirs(Context context) {
        PreferencesManager prefs = new PreferencesManager(context);
        Set<String> included = prefs.getIncludedFolders();
        Set<String> excluded = prefs.getExcludedFolders();
        boolean limitFolders = prefs.isLimitFolders();

        if (included == null) included = new HashSet<>();
        if (excluded == null) excluded = new HashSet<>();

        Set<String> scanDirs = new HashSet<>();
        if (limitFolders) {
            if (included.isEmpty()) return scanDirs;
            scanDirs.addAll(included);
        } else {
            scanDirs.addAll(included);
            String base = Environment.getExternalStorageDirectory().getAbsolutePath();
            scanDirs.add(base + "/Music");
            scanDirs.add(base + "/Download");
            scanDirs.add(base + "/Playlists");
        }
        scanDirs.removeAll(excluded);
        return scanDirs;
    }

    public static Map<String, Long> snapshotPlaylistFiles(Context context) {
        Map<String, Long> snapshot = new HashMap<>();
        Set<String> excluded = new PreferencesManager(context).getExcludedFolders();
        if (excluded == null) excluded = new HashSet<>();
        List<File> playlistFiles = new ArrayList<>();
        for (String dir : resolveScanDirs(context)) {
            File folder = new File(dir);
            if (folder.exists() && folder.isDirectory()) {
                collectPlaylistFiles(folder, playlistFiles, excluded, 0);
            }
        }
        for (File file : playlistFiles) {
            snapshot.put(file.getAbsolutePath(), file.lastModified());
        }
        return snapshot;
    }

    private static void collectPlaylistFiles(File dir, List<File> results,
                                              Set<String> excluded, int depth) {
        if (depth > MAX_DEPTH) return;
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                if (excluded.contains(f.getAbsolutePath())) continue;
                collectPlaylistFiles(f, results, excluded, depth + 1);
            } else {
                String name = f.getName().toLowerCase();
                for (String ext : PLAYLIST_EXTENSIONS) {
                    if (name.endsWith(ext)) {
                        results.add(f);
                        break;
                    }
                }
            }
        }
    }

    private static void importPlaylistFile(Context context, AudioRepository repo,
                                            File file, List<Audio> scannedAudio) {
        try {
            List<String> lines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }

            String fname = file.getName();
            int dot = fname.lastIndexOf('.');
            String playlistName = dot > 0 ? fname.substring(0, dot) : fname;
            List<PlaylistEntry> entries = new ArrayList<>();
            String currentTitle = null;
            String currentArtist = null;
            long currentDuration = 0;

            for (String raw : lines) {
                String line = raw.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("#EXTM3U")) continue;

                if (line.startsWith("#PLAYLIST:")) {
                    String parsed = line.substring(10).trim();
                    if (!parsed.isEmpty()) playlistName = parsed;
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

                if (line.startsWith("#")) continue;

                String resolvedUri = resolveUri(line, file.getParentFile());
                String matchedUri = resolvedUri;
                long matchedId = 0;
                String matchedTitle = null;
                String matchedArtist = null;
                long matchedDuration = 0;

                String normResolved = normalizePath(resolvedUri);
                for (Audio audio : scannedAudio) {
                    String audioPath = audio.getFilePath();
                    if (audioPath != null && normalizePath(audioPath).equals(normResolved)) {
                        matchedUri = audio.getUri();
                        matchedId = audio.getId();
                        matchedTitle = audio.getTitle();
                        matchedArtist = audio.getArtist();
                        matchedDuration = audio.getDuration();
                        break;
                    }
                }

                String fallbackTitle = matchedTitle;
                if (fallbackTitle == null) {
                    String uriPath = resolvedUri;
                    int slash = uriPath.lastIndexOf('/');
                    if (slash >= 0 && slash < uriPath.length() - 1) {
                        fallbackTitle = uriPath.substring(slash + 1);
                    } else {
                        fallbackTitle = uriPath;
                    }
                    int extDot = fallbackTitle.lastIndexOf('.');
                    if (extDot > 0) fallbackTitle = fallbackTitle.substring(0, extDot);
                }

                String title = currentTitle != null ? currentTitle : fallbackTitle;
                String artist = currentArtist != null ? currentArtist
                        : (matchedArtist != null ? matchedArtist : "");
                long duration = currentDuration > 0 ? currentDuration : matchedDuration;

                entries.add(new PlaylistEntry(matchedId, title, artist, duration, matchedUri));

                currentTitle = null;
                currentArtist = null;
                currentDuration = 0;
            }

            PlaylistEntity existing = repo.findPlaylistByName(playlistName);

            long playlistId;
            Map<String, PlaylistSongEntity> existingSongMap = new HashMap<>();

            if (existing != null) {
                playlistId = existing.getId();
                List<PlaylistSongEntity> existingSongs = repo.getPlaylistSongDao().getSongsForPlaylistSync((int) playlistId);
                for (PlaylistSongEntity song : existingSongs) {
                    String songUri = song.getUri();
                    if (songUri != null) {
                        existingSongMap.put(normalizePath(songUri), song);
                    }
                }
            } else {
                playlistId = repo.createPlaylist(playlistName);
            }

            int sortOrder = 0;
            for (PlaylistEntry entry : entries) {
                String normUri = normalizePath(entry.uri);
                PlaylistSongEntity existingEntity = existingSongMap.get(normUri);
                if (existingEntity != null) {
                    existingEntity.setTitle(entry.title);
                    existingEntity.setArtist(entry.artist);
                    existingEntity.setDuration(entry.duration);
                    existingEntity.setSortOrder(sortOrder++);
                    repo.getPlaylistSongDao().insert(existingEntity);
                } else {
                    PlaylistSongEntity entity = new PlaylistSongEntity(
                            (int) playlistId, entry.audioId, entry.title, entry.artist, "",
                            entry.duration, entry.uri, null, sortOrder++, System.currentTimeMillis());
                    repo.getPlaylistSongDao().insert(entity);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String normalizePath(String path) {
        try {
            return new File(path).getCanonicalPath().toLowerCase();
        } catch (Exception e) {
            return path.toLowerCase();
        }
    }

    private static String resolveUri(String path, File parentDir) {
        if (path.startsWith("content://") || path.startsWith("file://")) {
            return path;
        }
        File resolved = new File(path);
        if (!resolved.isAbsolute()) {
            resolved = new File(parentDir, path);
        }
        return resolved.getAbsolutePath();
    }

    private static class PlaylistEntry {
        final long audioId;
        final String title;
        final String artist;
        final long duration;
        final String uri;

        PlaylistEntry(long audioId, String title, String artist, long duration, String uri) {
            this.audioId = audioId;
            this.title = title;
            this.artist = artist;
            this.duration = duration;
            this.uri = uri;
        }
    }
}
