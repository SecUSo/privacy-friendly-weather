package org.secuso.privacyfriendlyweather.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.activities.MainActivity;
import org.secuso.privacyfriendlyweather.database.AppDatabase;
import org.secuso.privacyfriendlyweather.database.data.City;
import org.secuso.privacyfriendlyweather.database.data.CityToWatch;
import org.secuso.privacyfriendlyweather.ui.util.AutoCompleteCityTextViewGenerator;
import org.secuso.privacyfriendlyweather.ui.util.MyConsumer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonjuni on 04.01.17.
 */

public class AddLocationDialog extends DialogFragment {

    Activity activity;
    View rootView;
    AppDatabase database;

    private AutoCompleteTextView autoCompleteTextView;
    private AutoCompleteCityTextViewGenerator cityTextViewGenerator;
    City selectedCity;
    // TODO Cleanup
    private final List<City> allCities = new ArrayList<>();

    final int LIST_LIMIT = 100;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.dialog_add_location, null);

        rootView = view;

        builder.setView(view);
        builder.setIcon(R.drawable.app_icon);
        builder.setTitle(getActivity().getString(R.string.dialog_add_label));

        this.database = AppDatabase.getInstance(getActivity());
        final WebView webview = rootView.findViewById(R.id.webViewAddLocation);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setBackgroundColor(0x00000000);
        webview.setBackgroundResource(R.drawable.map_back);
        cityTextViewGenerator = new AutoCompleteCityTextViewGenerator(getContext(), database);
        autoCompleteTextView = rootView.findViewById(R.id.autoCompleteTvAddDialog);
        cityTextViewGenerator.generate(autoCompleteTextView, LIST_LIMIT, EditorInfo.IME_ACTION_DONE, new MyConsumer<City>() {
            @Override
            public void accept(City city) {
                selectedCity = city;
                if (selectedCity != null) {
                    //Hide keyboard to have more space
                    final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
                    //Show city on map
                    webview.loadUrl("file:///android_asset/map.html?lat=" + selectedCity.getLatitude() + "&lon=" + selectedCity.getLongitude());
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                performDone();
            }
        });

        builder.setPositiveButton(getActivity().getString(R.string.dialog_add_add_button), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                performDone();
            }
        });

        builder.setNegativeButton(getActivity().getString(R.string.dialog_add_close_button), null);

        return builder.create();
    }

    private void performDone() {
        CityToWatch newCity = convertCityToWatched();
        if (selectedCity == null) {
            Toast.makeText(activity, R.string.dialog_add_no_city_found, Toast.LENGTH_SHORT).show();
            return;
        }
        if (database != null && !database.cityToWatchDao().isCityWatched(selectedCity.getCityId())) {
            //insert id is needed immediately if city is ranked or deleted after insert
            newCity.setId((int) database.cityToWatchDao().addCityToWatch(newCity));
        }
        ((MainActivity) activity).addCityToList(newCity);
        dismiss();
    }

    private CityToWatch convertCityToWatched() {


        return new CityToWatch(
                database.cityToWatchDao().getMaxRank() + 1,
                selectedCity.getCountryCode(),
                0,
                selectedCity.getCityId(),
                selectedCity.getCityName(),
                selectedCity.getLongitude(),
                selectedCity.getLatitude()
        );
    }
}
