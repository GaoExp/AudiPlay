package exp.miniplayer.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "playlist_songs")
public class PlaylistSongEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int playlistId;
    private long audioId;
    private String title;
    private String artist;
    private String album;
    private long duration;
    private String uri;
    private String albumArt;
    private int sortOrder;
    private long addedAt;

    public PlaylistSongEntity(int playlistId, long audioId, String title, String artist,
                              String album, long duration, String uri, String albumArt,
                              int sortOrder, long addedAt) {
        this.playlistId = playlistId;
        this.audioId = audioId;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.uri = uri;
        this.albumArt = albumArt;
        this.sortOrder = sortOrder;
        this.addedAt = addedAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPlaylistId() { return playlistId; }
    public void setPlaylistId(int playlistId) { this.playlistId = playlistId; }

    public long getAudioId() { return audioId; }
    public void setAudioId(long audioId) { this.audioId = audioId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }

    public String getAlbum() { return album; }
    public void setAlbum(String album) { this.album = album; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public String getUri() { return uri; }
    public void setUri(String uri) { this.uri = uri; }

    public String getAlbumArt() { return albumArt; }
    public void setAlbumArt(String albumArt) { this.albumArt = albumArt; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }

    public long getAddedAt() { return addedAt; }
    public void setAddedAt(long addedAt) { this.addedAt = addedAt; }
}
