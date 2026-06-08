package exp.miniplayer.ui.system_picker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import exp.miniplayer.MainActivity;
import exp.miniplayer.R;
import exp.miniplayer.model.Audio;

public class SystemPickerFragment extends Fragment {

    private final ActivityResultLauncher<Intent> pickAudioLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                Intent data = result.getData();
                if (data == null) return;
                List<Audio> pickedAudios = new ArrayList<>();
                if (data.getData() != null) {
                    pickedAudios.add(createAudioFromUri(data.getData()));
                } else if (data.getClipData() != null) {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        Uri uri = data.getClipData().getItemAt(i).getUri();
                        pickedAudios.add(createAudioFromUri(uri));
                    }
                }
                if (!pickedAudios.isEmpty() && requireActivity() instanceof MainActivity) {
                    ((MainActivity) requireActivity()).playFromSongs(pickedAudios, 0);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_system_picker, container, false);
        CardView pickerCard = view.findViewById(R.id.picker_card);
        pickerCard.setOnClickListener(v -> openPicker());
        return view;
    }

    private void openPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickAudioLauncher.launch(intent);
    }

    private Audio createAudioFromUri(Uri uri) {
        String title = "Audio";
        try (Cursor cursor = requireContext().getContentResolver().query(
                uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    title = cursor.getString(nameIndex);
                }
            }
        } catch (Exception ignored) {}
        return new Audio(
                System.currentTimeMillis(), title, "", "", 0,
                uri.toString(), null, System.currentTimeMillis());
    }
}
