package exp.miniplayer.ui.other_audio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import exp.miniplayer.MainActivity;
import exp.miniplayer.R;
import exp.miniplayer.adapter.SongAdapter;
import exp.miniplayer.model.Audio;
import exp.miniplayer.utils.MusicScanner;

import java.util.List;

public class OtherAudioFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_audio, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        emptyText = view.findViewById(R.id.empty_text);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadOtherAudio();
        return view;
    }

    private void loadOtherAudio() {
        new Thread(() -> {
            List<Audio> otherAudio = MusicScanner.scanAllAudio(requireContext());
            requireActivity().runOnUiThread(() -> {
                if (otherAudio.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                    SongAdapter adapter = new SongAdapter(requireContext(), otherAudio);
                    adapter.setOnItemClickListener((audio, position) -> {
                        if (requireActivity() instanceof MainActivity) {
                            ((MainActivity) requireActivity()).playFromSongs(otherAudio, position);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }
            });
        }).start();
    }

}
