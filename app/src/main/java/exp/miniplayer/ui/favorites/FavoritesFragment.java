package exp.miniplayer.ui.favorites;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import exp.miniplayer.R;
import exp.miniplayer.adapter.SongAdapter;
import exp.miniplayer.data.AudioRepository;
import exp.miniplayer.database.FavoriteEntity;
import exp.miniplayer.model.Audio;
import exp.miniplayer.ui.nowplaying.NowPlayingActivity;
import exp.miniplayer.utils.QueueHolder;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel viewModel;
    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private View emptyView;
    private AudioRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.favorites_recycler_view);
        emptyView = view.findViewById(R.id.empty_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        viewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        repository = viewModel.getRepository();

        viewModel.getFavoriteEntities().observe(getViewLifecycleOwner(), favorites -> {
            if (favorites == null || favorites.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                List<Audio> audioList = new ArrayList<>();
                for (FavoriteEntity entity : favorites) {
                    Audio audio = new Audio(entity.getAudioId(), entity.getTitle(),
                            entity.getArtist(), entity.getAlbum(), entity.getDuration(),
                            entity.getUri(), entity.getAlbumArt(), entity.getAddedAt());
                    audioList.add(audio);
                }
                adapter = new SongAdapter(requireContext(), audioList);
                adapter.setOnItemClickListener((audio, position) -> {
                    QueueHolder.setQueue(audioList, position);
                    Intent intent = new Intent(requireContext(), NowPlayingActivity.class);
                    startActivity(intent);
                });
                adapter.setOnItemLongClickListener((audio, position1) -> {
                    PopupMenu popup = new PopupMenu(requireContext(), recyclerView);
                    popup.getMenuInflater().inflate(R.menu.song_options_menu, popup.getMenu());
                    popup.getMenu().findItem(R.id.action_add_favorite)
                            .setTitle(getString(R.string.remove_from_favorites));
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.action_add_favorite) {
                            viewModel.removeFavorite(audio.getId());
                            Toast.makeText(requireContext(), "Removed from favorites",
                                    Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    });
                    popup.show();
                    return true;
                });
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
