package org.secuso.privacyfriendlyweather.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.activities.CreateKeyActivity;

public class AreYouSureFragment extends Fragment {

    Button shared;
    Button personal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_are_you_sure, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shared = view.findViewById(R.id.button_abort_personal_key);
        personal = view.findViewById(R.id.button_return_to_key);

        shared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CreateKeyActivity) getActivity()).leave();
            }
        });

        personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CreateKeyActivity) getActivity()).onBackPressed();
            }
        });

    }


}
