package org.secuso.privacyfriendlyweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.secuso.privacyfriendlyweather.database.City;
import org.secuso.privacyfriendlyweather.database.CityToWatch;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;

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
    private ArrayAdapter<City> adapter;
    City selectedCity;
    // TODO Cleanup
    private final List<City> allCities = new ArrayList<>();

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
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(getActivity().getString(R.string.dialog_add_label));

        this.database = PFASQLiteHelper.getInstance(getActivity());


//        new AsyncTask<Void, Void, List<City>>() {
//            @Override
//            protected List<City> doInBackground(Void... params) {
//
//                List<City> cities = new ArrayList<City>();
//                cities.addAll(database.getAllCities());
//
//                return cities;
//            }
//
//            @Override
//            protected void onPostExecute(List<City> cities) {
//                super.onPostExecute(cities);
//
//                setCities(cities);
//            }
//        }.execute();

        autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTvAddDialog);

        adapter = new ArrayAdapter<City>(getContext(), android.R.layout.simple_list_item_1, new ArrayList<City>());

        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                final int LIST_LIMIT = 8;
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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

        builder.setPositiveButton(getActivity().getString(R.string.dialog_add_add_button), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                addCity();
                activity.recreate();
                dismiss();

            }
        });

        builder.setNegativeButton(getActivity().getString(R.string.dialog_add_close_button), null);

        return builder.create();
    }

// TODO Cleanup
//    private void setCities(List<City> cities) {
//        if(this.allCities.size() == 0) {
//            this.allCities.addAll(cities);
//        }
//    }

    //TODO setRank
    //TODO Update the list
    public void addCity() {
        String postCode = "-";

        try {
            postCode = selectedCity.getPostalCode();
        } catch (NullPointerException e) {

        }

        database.addCityToWatch(new CityToWatch(
                15,
                postCode,
                selectedCity.getCountryCode(),
                -1,
                selectedCity.getCityId(),
                selectedCity.getCityName()
        ));
    }

}
