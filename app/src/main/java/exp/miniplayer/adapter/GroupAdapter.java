package exp.miniplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import exp.miniplayer.R;
import exp.miniplayer.utils.TimeUtils;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    public static class GroupItem {
        public final String name;
        public final int count;
        public final long totalDuration;
        public final long totalSize;

        public GroupItem(String name, int count) {
            this(name, count, 0, 0);
        }

        public GroupItem(String name, int count, long totalDuration, long totalSize) {
            this.name = name;
            this.count = count;
            this.totalDuration = totalDuration;
            this.totalSize = totalSize;
        }
    }

    private final List<GroupItem> items;
    private final OnGroupClickListener listener;

    public interface OnGroupClickListener {
        void onGroupClick(int position, GroupItem item);
    }

    public GroupAdapter(List<GroupItem> items, OnGroupClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupItem item = items.get(position);
        holder.nameText.setText(item.name);
        holder.countText.setText(holder.itemView.getContext()
                .getString(R.string.song_count, item.count));

        if (item.totalDuration > 0 || item.totalSize > 0) {
            holder.statsText.setVisibility(View.VISIBLE);
            String duration = TimeUtils.formatDuration(item.totalDuration);
            String size = formatSize(item.totalSize);
            holder.statsText.setText(holder.itemView.getContext()
                    .getString(R.string.folder_stats, duration, size));
        } else {
            holder.statsText.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGroupClick(position, item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameText;
        final TextView countText;
        final TextView statsText;

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.group_name);
            countText = itemView.findViewById(R.id.group_count);
            statsText = itemView.findViewById(R.id.group_stats);
        }
    }

    public static String formatSize(long bytes) {
        if (bytes <= 0) return "0 B";
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = bytes;
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        if (unitIndex == 0) return String.format("%d B", (int) size);
        return String.format("%.1f %s", size, units[unitIndex]);
    }
}
