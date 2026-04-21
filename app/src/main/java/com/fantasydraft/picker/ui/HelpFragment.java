package com.fantasydraft.picker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fantasydraft.picker.R;

/**
 * Fragment displaying help documentation, grading system, and how to use the app.
 */
public class HelpFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        
        // Set version info
        TextView versionText = view.findViewById(R.id.text_help_version);
        if (versionText != null) {
            try {
                String versionName = requireContext().getPackageManager()
                        .getPackageInfo(requireContext().getPackageName(), 0).versionName;
                int versionCode = requireContext().getPackageManager()
                        .getPackageInfo(requireContext().getPackageName(), 0).versionCode;
                versionText.setText("Version " + versionName + " (Build " + versionCode + ")");
            } catch (Exception e) {
                versionText.setText("Version unknown");
            }
        }
        
        return view;
    }
}
