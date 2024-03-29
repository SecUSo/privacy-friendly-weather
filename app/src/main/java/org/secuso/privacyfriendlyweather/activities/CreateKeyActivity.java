package org.secuso.privacyfriendlyweather.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.ui.AreYouSureFragment;

//TODO change text depending on intent, already got "429" true and false
public class CreateKeyActivity extends AppCompatActivity {

    public static boolean active = false;

    RelativeLayout layout;
    EditText personalKeyField;
    Button keyButton;
    Button abortButton;
    Boolean fragmentShown = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_key);
        layout = findViewById(R.id.add_key_layout);
        personalKeyField = findViewById(R.id.owm_key_field);
        keyButton = findViewById(R.id.set_owm_key_button);
        abortButton = findViewById(R.id.abort_button);
        TextView introText = findViewById(R.id.explanation_text_owm_key);
        boolean limitReached = getIntent().getBooleanExtra("429", false);
        if (!limitReached) {
            introText.setText(R.string.explanation_owm_key_on_start);
        }

        keyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentValue = personalKeyField.getText().toString().replaceAll("[\\s\\u0085\\p{Z}]", "");
                Log.d("debugtag", "currentfalue3: " + currentValue);
                if (currentValue != null && currentValue.length() == 32) {

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("API_key_value", currentValue);
                    editor.commit();
                    leave();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.insert_correct_owm_key, Toast.LENGTH_LONG).show();
                }
            }
        });

        abortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        if (getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void areYouSure() {
        fragmentShown = true;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.are_you_sure_fragment, new AreYouSureFragment());
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        if (fragmentShown) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.remove(getSupportFragmentManager().findFragmentById(R.id.are_you_sure_fragment));
            ft.commit();
            fragmentShown = false;
        } else {
            areYouSure();
        }
    }

    public void leave() {
        AppPreferencesManager prefManager = new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(this));
        prefManager.setAskedForOwmKey(true);
        ForecastCityActivity.stopTurning = true;
        if (isTaskRoot()) {
            Intent mainIntent = new Intent(getApplicationContext(), ForecastCityActivity.class);
            startActivity(mainIntent);
        } else {
            super.onBackPressed();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
