package exp.miniplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Audio implements Parcelable {
    private long id;
    private String title;
    private String artist;
    private String album;
    private long duration;
    private String uri;
    private String albumArt;
    private long dateAdded;
    private String filePath;
    private long fileSize;

    public Audio(long id, String title, String artist, String album,
                 long duration, String uri, String albumArt, long dateAdded) {
        this.id = id;
        this.title = title != null && !title.isEmpty() ? title : "Unknown Title";
        this.artist = artist != null && !artist.isEmpty() ? artist : "Unknown Artist";
        this.album = album != null && !album.isEmpty() ? album : "Unknown Album";
        this.duration = duration;
        this.uri = uri;
        this.albumArt = albumArt;
        this.dateAdded = dateAdded;
        this.filePath = "";
        this.fileSize = 0;
    }

    protected Audio(Parcel in) {
        id = in.readLong();
        title = in.readString();
        artist = in.readString();
        album = in.readString();
        duration = in.readLong();
        uri = in.readString();
        albumArt = in.readString();
        dateAdded = in.readLong();
        filePath = in.readString();
        fileSize = in.readLong();
    }

    public static final Creator<Audio> CREATOR = new Creator<Audio>() {
        @Override
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        @Override
        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeString(album);
        dest.writeLong(duration);
        dest.writeString(uri);
        dest.writeString(albumArt);
        dest.writeLong(dateAdded);
        dest.writeString(filePath);
        dest.writeLong(fileSize);
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

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

    public long getDateAdded() { return dateAdded; }
    public void setDateAdded(long dateAdded) { this.dateAdded = dateAdded; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
}
