package org.secuso.privacyfriendlyweather.activities;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.City;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.preferences.AppPreferencesManager;
import org.secuso.privacyfriendlyweather.ui.AutoCompleteCityTextViewGenerator;
import org.secuso.privacyfriendlyweather.weather_api.IHttpRequestForRadiusSearch;
import org.secuso.privacyfriendlyweather.weather_api.open_weather_map.OwmHttpRequestForRadiusSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity provides the functionality to search the best weather around a given location.
 */
public class RadiusSearchActivity extends BaseActivity {

    /**
     * Visual components
     */
    private AppPreferencesManager prefManager;
    private AutoCompleteTextView edtLocation;
    private SeekBar sbEdgeLength;
    private TextView tvEdgeLengthValue;
    private SeekBar sbNumReturns;
    private TextView tvNumReturnsValue;
    private Button btnSearch;
    private ArrayAdapter<City> adapter;

    int edgeRange;
    int minEdgeLength;

    int numberOfReturnsRange;
    int minNumberOfReturns;

    /**
     * Other components
     */
    private PFASQLiteHelper dbHelper;
    private City dropdownSelectedCity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radius_search);
        overridePendingTransition(0, 0);


        dbHelper = PFASQLiteHelper.getInstance(this);
        initialize();
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_radius;
    }

    /**
     * Initializes the visual components / the view.
     */
    private void initialize() {
        // Constants
        final int MAX_EDGE_LENGTH_IN_KM = 150;
        final int MIN_EDGE_LENGTH_IN_KM = 20;
        final int MAX_NUMBER_OF_RETURNS = 10;
        final int MIN_NUMBER_OF_RETURNS = 2;
        final int DEFAULT_NUMBER_OF_RETURNS = 3;
        final String FORMAT_EDGE_LENGTH_VALUE = "%s %s";

        // Values which are necessary down below
        prefManager = new AppPreferencesManager(PreferenceManager.getDefaultSharedPreferences(this));
        edgeRange = Math.round(prefManager.convertDistanceFromKilometers(MAX_EDGE_LENGTH_IN_KM - MIN_EDGE_LENGTH_IN_KM));
        minEdgeLength = Math.round(prefManager.convertDistanceFromKilometers(MIN_EDGE_LENGTH_IN_KM));
        numberOfReturnsRange = MAX_NUMBER_OF_RETURNS - MIN_NUMBER_OF_RETURNS;
        minNumberOfReturns = MIN_NUMBER_OF_RETURNS;

        // Visual components
        //AutoCompleteCityTextViewGenerator generator = new AutoCompleteCityTextViewGenerator(this, dbHelper);
        edtLocation = (AutoCompleteTextView) findViewById(R.id.radius_search_edt_location);

        adapter = new ArrayAdapter<City>(getBaseContext(), android.R.layout.simple_list_item_1, new ArrayList<City>());

        edtLocation.setAdapter(adapter);

        edtLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final int LIST_LIMIT = 8;
                dropdownSelectedCity = null;
                if (dbHelper != null) {
                    String current = edtLocation.getText().toString();
                    if (current.length() > 2) {

                        //List<City> cities = database.getCitiesWhereNameLike(current, allCities, current.length());
                        List<City> cities = dbHelper.getCitiesWhereNameLike(current, LIST_LIMIT);
                        //TODO Add Postal Code
                        adapter.clear();
                        adapter.addAll(cities);
                        edtLocation.showDropDown();
                    } else {
                        edtLocation.dismissDropDown();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        edtLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dropdownSelectedCity = (City)parent.getAdapter().getItem(position);
            }
        });

        //generator.getInstance(edtLocation, 8, dropdownSelectedCity);
//        edtLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                dropdownSelectedCity = (City) parent.getItemAtPosition(position);
//                // Also close the keyboard
//                InputMethodManager imm =
//                        (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//            }
//        });

        sbEdgeLength = (SeekBar)

                findViewById(R.id.radius_search_sb_edge_length);

        tvEdgeLengthValue = (TextView)

                findViewById(R.id.radius_search_tv_edge_length_value);

        sbNumReturns = (SeekBar)

                findViewById(R.id.radius_search_sb_num_returns);

        tvNumReturnsValue = (TextView)

                findViewById(R.id.radius_search_tv_num_returns_value);

        btnSearch = (Button)

                findViewById(R.id.radius_search_btn_search);

        // Set properties of seek bars and the text of the corresponding text views
        sbEdgeLength.setMax(edgeRange);
        sbEdgeLength.setProgress(((edgeRange + minEdgeLength) >> 1) - minEdgeLength);
        tvEdgeLengthValue.setText(
                String.format(FORMAT_EDGE_LENGTH_VALUE, sbEdgeLength.getProgress() + minEdgeLength, prefManager.getDistanceUnit())
        );

        sbNumReturns.setMax(numberOfReturnsRange);
        sbNumReturns.setProgress(DEFAULT_NUMBER_OF_RETURNS - minNumberOfReturns);
        tvNumReturnsValue.setText(String.valueOf(sbNumReturns.getProgress() + minNumberOfReturns));

        // On change of the seek bars set the text of the corresponding text views
        sbEdgeLength.setOnSeekBarChangeListener(new

                OnSeekBarEdgeLengthChange()

        );
        sbNumReturns.setOnSeekBarChangeListener(new

                OnSeekBarNumberOfReturnsChange()

        );

        // Set the click event on the button
        btnSearch.setOnClickListener(new View.OnClickListener()

                                     {
                                         @Override
                                         public void onClick(View v) {
                                             handleOnButtonSearchClick();
                                         }
                                     }

        );
    }

    /**
     * This method handles the click event on the 'Search' button.
     */

    private void handleOnButtonSearchClick() {
        // Retrieve all necessary inputs (convert the edgeLength if necessary)
        int edgeLength = sbEdgeLength.getProgress() + minEdgeLength;
        int numberOfReturnCities = sbNumReturns.getProgress() + minNumberOfReturns;
        if (prefManager.isDistanceUnitMiles()) {
            edgeLength = Math.round(prefManager.convertMilesInKm(edgeLength));
        }

        //List<City> allCities = dbHelper.getAllCities();

        // Procedure for retrieving the city (only necessary if no item from the drop down list
        // was selected)
        City city = dropdownSelectedCity;
        if (dropdownSelectedCity == null) {
            List<City> foundCities = dbHelper.getCitiesWhereNameLike(edtLocation.getText().toString(), 2);
            //List<City> foundCities = dbHelper.getCitiesWhereNameLike(edtLocation.getText().toString(), 2);
            // 1) No city found
            if (foundCities.size() == 0) {
                Toast.makeText(RadiusSearchActivity.this, R.string.dialog_add_no_city_found, Toast.LENGTH_LONG).show();
            }
            // 2) 1 city found,
            else if (foundCities.size() == 1) {
                city = foundCities.get(0);
            }
            // 3) > 1 cities found
            else {
                Toast.makeText(RadiusSearchActivity.this, R.string.dialog_add_too_many_cities_found, Toast.LENGTH_LONG).show();
            }
        }

        if (city != null) {
            IHttpRequestForRadiusSearch radiusSearchRequest = new OwmHttpRequestForRadiusSearch(getApplicationContext());
            radiusSearchRequest.perform(city.getCityId(), edgeLength, numberOfReturnCities);
        }
    }

    /**
     * Implements the logic for the SeekBar to set the edge length.
     */
    private class OnSeekBarEdgeLengthChange implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            String text = String.format("%s %s", progress + minEdgeLength, prefManager.getDistanceUnit());
            tvEdgeLengthValue.setText(text);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

    /**
     * Implements the logic for the SeekBar to set the number of returned cities.
     */
    private class OnSeekBarNumberOfReturnsChange implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            tvNumReturnsValue.setText(String.valueOf(progress + minNumberOfReturns));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

}
