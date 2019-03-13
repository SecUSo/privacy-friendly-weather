package org.secuso.privacyfriendlyweather.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.City;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
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
    PFASQLiteHelper database;

    private AutoCompleteTextView autoCompleteTextView;
    private AutoCompleteCityTextViewGenerator cityTextViewGenerator;
    City selectedCity;
    // TODO Cleanup
    private final List<City> allCities = new ArrayList<>();

    final int LIST_LIMIT = 8;

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

        this.database = PFASQLiteHelper.getInstance(getActivity());

        cityTextViewGenerator = new AutoCompleteCityTextViewGenerator(getContext(), database);
        autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTvAddDialog);
        cityTextViewGenerator.generate(autoCompleteTextView, LIST_LIMIT, EditorInfo.IME_ACTION_DONE, new MyConsumer<City>() {
            @Override
            public void accept(City city) {
                selectedCity = city;
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
        if (selectedCity == null) {
            Toast.makeText(activity, R.string.dialog_add_no_city_found, Toast.LENGTH_SHORT).show();
            return;
        }
        if(database != null && !database.isCityWatched(selectedCity.getCityId())) {
            addCity();
        }

        activity.recreate();
        dismiss();
    }

    //TODO setRank
    public void addCity() {
        String postCode = "-";
        try {
            postCode = selectedCity.getPostalCode();
        } catch (NullPointerException e) {

        }

        new AsyncTask<CityToWatch, Void, Void>() {
            @Override
            protected Void doInBackground(CityToWatch... params) {
                database.addCityToWatch(params[0]);
                return null;
            }
        }.doInBackground(new CityToWatch(
                15,
                postCode,
                selectedCity.getCountryCode(),
                -1,
                selectedCity.getCityId(),
                selectedCity.getCityName()
        ));
    }

}
