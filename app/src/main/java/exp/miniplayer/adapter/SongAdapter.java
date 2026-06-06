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
import exp.miniplayer.model.Audio;
import exp.miniplayer.utils.TimeUtils;

import java.io.InputStream;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final List<Audio> audioList;
    private final Context context;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(Audio audio, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(Audio audio, int position);
    }

    public SongAdapter(Context context, List<Audio> audioList) {
        this.context = context;
        this.audioList = audioList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    public void updateData(List<Audio> newList) {
        audioList.clear();
        audioList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Audio audio = audioList.get(position);
        holder.titleText.setText(audio.getTitle());
        holder.artistText.setText(audio.getArtist());
        holder.durationText.setText(TimeUtils.formatDuration(audio.getDuration()));
        loadAlbumArt(audio.getAlbumArt(), holder.albumArt);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(audio, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                return onItemLongClickListener.onItemLongClick(audio, position);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public List<Audio> getCurrentList() {
        return new ArrayList<>(audioList);
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

    static class SongViewHolder extends RecyclerView.ViewHolder {
        final ImageView albumArt;
        final TextView titleText;
        final TextView artistText;
        final TextView durationText;

        SongViewHolder(View itemView) {
            super(itemView);
            albumArt = itemView.findViewById(R.id.song_album_art);
            titleText = itemView.findViewById(R.id.song_title);
            artistText = itemView.findViewById(R.id.song_artist);
            durationText = itemView.findViewById(R.id.song_duration);
        }
    }
}
