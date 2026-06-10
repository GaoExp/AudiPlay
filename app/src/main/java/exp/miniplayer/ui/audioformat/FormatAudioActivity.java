package exp.miniplayer.ui.audioformat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exp.miniplayer.R;
import exp.miniplayer.ui.settings.SettingsViewModel;
import exp.miniplayer.utils.PreferencesManager;

public class FormatAudioActivity extends AppCompatActivity {

    private PreferencesManager prefs;
    private LinearLayout sectionContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_format_audio);

        prefs = new PreferencesManager(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        sectionContainer = findViewById(R.id.section_container);

        findViewById(R.id.select_all).setOnClickListener(v -> {
            Set<String> all = new HashSet<>();
            for (List<String> list : SettingsViewModel.getFormatGroups().values()) {
                all.addAll(list);
            }
            prefs.setAudioFormats(all);
            updateCheckboxes(all);
        });

        findViewById(R.id.deselect_all).setOnClickListener(v -> {
            prefs.setAudioFormats(new HashSet<>());
            updateCheckboxes(new HashSet<>());
        });

        buildSections();
    }

    private void buildSections() {
        Set<String> enabled = prefs.getAudioFormats();
        LayoutInflater inflater = getLayoutInflater();

        for (Map.Entry<String, List<String>> entry : SettingsViewModel.getFormatGroups().entrySet()) {
            View header = inflater.inflate(R.layout.item_format_header, sectionContainer, false);
            TextView title = header.findViewById(R.id.section_title);
            TextView indicator = header.findViewById(R.id.section_indicator);

            title.setText(entry.getKey());

            LinearLayout sectionBody = new LinearLayout(this);
            sectionBody.setOrientation(LinearLayout.VERTICAL);
            sectionBody.setVisibility(View.GONE);

            header.setOnClickListener(v -> {
                boolean expanded = sectionBody.getVisibility() == View.VISIBLE;
                sectionBody.setVisibility(expanded ? View.GONE : View.VISIBLE);
                indicator.setText(expanded ? "\u22C1" : "\u22C0");
            });

            sectionContainer.addView(header);
            sectionContainer.addView(sectionBody);

            for (String format : entry.getValue()) {
                CheckBox checkBox = (CheckBox) inflater.inflate(
                        R.layout.item_format_checkbox, sectionBody, false);
                checkBox.setText("." + format);
                checkBox.setChecked(enabled.contains(format));

                int playability = SettingsViewModel.getFormatPlayability(format);
                int colorRes;
                if (playability == SettingsViewModel.PLAYABLE) {
                    colorRes = R.color.green;
                } else if (playability == SettingsViewModel.MAYBE) {
                    colorRes = R.color.orange;
                } else {
                    colorRes = R.color.red;
                }
                checkBox.setTextColor(getColor(colorRes));
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Set<String> current = prefs.getAudioFormats();
                    if (isChecked) {
                        current.add(format);
                    } else {
                        current.remove(format);
                    }
                    prefs.setAudioFormats(current);
                });
                sectionBody.addView(checkBox);
            }
        }
    }

    private void updateCheckboxes(Set<String> enabled) {
        for (int i = 0; i < sectionContainer.getChildCount(); i++) {
            View child = sectionContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout section = (LinearLayout) child;
                for (int j = 0; j < section.getChildCount(); j++) {
                    View item = section.getChildAt(j);
                    if (item instanceof CheckBox) {
                        String text = ((CheckBox) item).getText().toString();
                        String format = text.startsWith(".") ? text.substring(1) : text;
                        ((CheckBox) item).setChecked(enabled != null && enabled.contains(format));
                    }
                }
            }
        }
    }
}
