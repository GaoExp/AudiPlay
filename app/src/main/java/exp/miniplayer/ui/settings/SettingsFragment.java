package exp.miniplayer.ui.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import exp.miniplayer.R;

public class SettingsFragment extends Fragment {

    private SettingsViewModel viewModel;
    private SwitchCompat darkModeSwitch;
    private SwitchCompat followSystemSwitch;
    private SwitchCompat keepScreenOnSwitch;
    private Spinner defaultRepeatSpinner;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        darkModeSwitch = view.findViewById(R.id.dark_mode_switch);
        followSystemSwitch = view.findViewById(R.id.follow_system_switch);
        keepScreenOnSwitch = view.findViewById(R.id.keep_screen_on_switch);
        defaultRepeatSpinner = view.findViewById(R.id.default_repeat_spinner);

        viewModel.getDarkMode().observe(getViewLifecycleOwner(), darkModeSwitch::setChecked);
        viewModel.getFollowSystem().observe(getViewLifecycleOwner(), followSystemSwitch::setChecked);
        viewModel.getKeepScreenOn().observe(getViewLifecycleOwner(), keepScreenOnSwitch::setChecked);
        viewModel.getDefaultRepeatMode().observe(getViewLifecycleOwner(), mode -> {
            defaultRepeatSpinner.setSelection(mode);
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setDarkMode(isChecked);
        });

        followSystemSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setFollowSystem(isChecked);
            darkModeSwitch.setEnabled(!isChecked);
        });

        keepScreenOnSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setKeepScreenOn(isChecked);
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.repeat_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        defaultRepeatSpinner.setAdapter(adapter);
        defaultRepeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setDefaultRepeatMode(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        darkModeSwitch.setEnabled(!followSystemSwitch.isChecked());
    }
}
