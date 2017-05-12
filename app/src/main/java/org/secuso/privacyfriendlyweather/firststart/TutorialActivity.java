package org.secuso.privacyfriendlyweather.firststart;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.activities.ForecastCityActivity;
import org.secuso.privacyfriendlyweather.database.City;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.preferences.PrefManager;
import org.secuso.privacyfriendlyweather.services.UpdateDataService;

import java.util.ArrayList;
import java.util.List;

/**
 * Class structure taken from tutorial at http://www.androidhive.info/2016/05/android-build-intro-slider-app/
 * @author Karola Marky
 * @version 20161214
 */

public class TutorialActivity extends AppCompatActivity {

    private Button doneButton;
    private PrefManager prefManager;

    PFASQLiteHelper database;

    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<City> adapter;
    City selectedCity;
    final int LIST_LIMIT = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        database = PFASQLiteHelper.getInstance(this);
        if (!prefManager.isFirstTimeLaunch()) {
            startActivity(new Intent(TutorialActivity.this, ForecastCityActivity.class));
            finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_tutorial);

        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTvAddFirstStart);

        adapter = new ArrayAdapter<City>(getBaseContext(), android.R.layout.simple_list_item_1, new ArrayList<City>());

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    performDone();
                    return true;
                }
                return false;
            }
        });

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                selectedCity = null;
                if (database != null) {
                    String current = autoCompleteTextView.getText().toString();
                    if (current.length() > 2) {

                        //List<City> cities = database.getCitiesWhereNameLike(current, allCities, current.length());
                        List<City> cities = database.getCitiesWhereNameLike(current, LIST_LIMIT);
                        //TODO Add Postal Code
                        adapter.clear();
                        adapter.addAll(cities);
                        autoCompleteTextView.showDropDown();
                    } else {
                        autoCompleteTextView.dismissDropDown();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = (City) parent.getItemAtPosition(position);
            }
        });

        doneButton = (Button) findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDone();
            }
        });

    }

    private void performDone() {
        if (selectedCity == null) {
            String current = autoCompleteTextView.getText().toString();
            if (current.length() > 2) {
                List<City> cities = database.getCitiesWhereNameLike(current, LIST_LIMIT);
                if (cities.size() == 1) {
                    selectedCity = cities.get(0);
                    launchHomeScreen();
                    return;
                }
            }

            //TODO Add to strings
            Toast.makeText(getBaseContext(), "Please choose a location", Toast.LENGTH_SHORT).show();
        } else {
            launchHomeScreen();
        }
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        prefManager.setDefaultLocation(selectedCity.getCityId());
        addCity();
        startActivity(new Intent(TutorialActivity.this, ForecastCityActivity.class));
        finish();
    }

    public void addCity() {
        if (selectedCity != null) {
            database.addCityToWatch(new CityToWatch(
                    15,
                    selectedCity.getPostalCode(),
                    selectedCity.getCountryCode(),
                    -1,
                    selectedCity.getCityId(),
                    selectedCity.getCityName()
            ));
        }
        Intent intent = new Intent(this, UpdateDataService.class);
        intent.setAction(UpdateDataService.UPDATE_CURRENT_WEATHER_ACTION);
        startService(intent);
    }
}