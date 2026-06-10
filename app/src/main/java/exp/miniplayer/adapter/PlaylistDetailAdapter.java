package exp.miniplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import exp.miniplayer.R;
import exp.miniplayer.database.PlaylistSongEntity;
import exp.miniplayer.utils.TimeUtils;

import java.io.InputStream;
import java.util.List;

public class PlaylistDetailAdapter extends RecyclerView.Adapter<PlaylistDetailAdapter.ViewHolder> {

    private final Context context;
    private final List<PlaylistSongEntity> songList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(PlaylistSongEntity song, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(PlaylistSongEntity song, int position);
    }

    public PlaylistDetailAdapter(Context context, List<PlaylistSongEntity> songList) {
        this.context = context;
        this.songList = songList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PlaylistSongEntity song = songList.get(position);
        holder.titleText.setText(song.getTitle());
        holder.artistText.setText(song.getArtist());
        holder.durationText.setText(TimeUtils.formatDuration(song.getDuration()));
        loadAlbumArt(song.getAlbumArt(), holder.albumArt);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(song, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                return onItemLongClickListener.onItemLongClick(song, position);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    private void loadAlbumArt(String albumArtUri, ImageView imageView) {
        if (albumArtUri == null) {
            imageView.setImageResource(R.drawable.ic_album_default);
            return;
        }
        try {
            Uri uri = Uri.parse(albumArtUri);
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView.setImageResource(R.drawable.ic_album_default);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView albumArt;
        final TextView titleText;
        final TextView artistText;
        final TextView durationText;

        ViewHolder(View itemView) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.song_album_art);
            titleText = itemView.findViewById(R.id.song_title);
            artistText = itemView.findViewById(R.id.song_artist);
            durationText = itemView.findViewById(R.id.song_duration);
        }
    }
}
