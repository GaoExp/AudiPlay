package exp.miniplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import exp.miniplayer.R;
import exp.miniplayer.database.PlaylistEntity;
import exp.miniplayer.utils.TimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private final Context context;
    private final List<PlaylistEntity> playlistList;
    private Map<Integer, Integer> songCounts = new HashMap<>();
    private Map<Integer, String> playlistStats = new HashMap<>();
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(PlaylistEntity playlist, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(PlaylistEntity playlist, int position);
    }

    public PlaylistAdapter(Context context, List<PlaylistEntity> playlistList) {
        this.context = context;
        this.playlistList = playlistList;
    }

    public void setSongCounts(Map<Integer, Integer> counts) {
        this.songCounts = counts != null ? counts : new HashMap<>();
        notifyDataSetChanged();
    }

    public void setPlaylistStats(Map<Integer, String> stats) {
        this.playlistStats = stats != null ? stats : new HashMap<>();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.onItemLongClickListener = listener;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        PlaylistEntity playlist = playlistList.get(position);
        holder.nameText.setText(playlist.getName());
        Integer count = songCounts.get(playlist.getId());
        if (count != null && count > 0) {
            holder.songCountText.setText(context.getResources()
                    .getQuantityString(R.plurals.playlist_song_count, count, count));
        } else if (count != null && count == 0) {
            holder.songCountText.setText(context.getString(R.string.playlist_empty));
        } else {
            holder.songCountText.setText(context.getString(R.string.playlist_empty));
        }

        String stats = playlistStats.get(playlist.getId());
        if (stats != null) {
            holder.statsText.setVisibility(View.VISIBLE);
            holder.statsText.setText(stats);
        } else {
            holder.statsText.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(playlist, position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (onItemLongClickListener != null) {
                return onItemLongClickListener.onItemLongClick(playlist, position);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView songCountText;
        final TextView statsText;

        PlaylistViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.playlist_name);
            songCountText = itemView.findViewById(R.id.playlist_song_count);
            statsText = itemView.findViewById(R.id.playlist_stats);
        }
    }
}
