package exp.miniplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {

    public static class GroupItem {
        public final String name;
        public final int count;

        public GroupItem(String name, int count) {
            this.name = name;
            this.count = count;
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
                .inflate(exp.miniplayer.R.layout.item_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroupItem item = items.get(position);
        holder.nameText.setText(item.name);
        holder.countText.setText(holder.itemView.getContext()
                .getString(exp.miniplayer.R.string.song_count, item.count));
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

        ViewHolder(View itemView) {
            super(itemView);
            nameText = itemView.findViewById(exp.miniplayer.R.id.group_name);
            countText = itemView.findViewById(exp.miniplayer.R.id.group_count);
        }
    }
}
