package org.secuso.privacyfriendlyweather.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.secuso.privacyfriendlyweather.orm.City;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides an AutoCompleteTextView which shows a drop down list with cities that match
 * the input string.
 */
public class AutoCompleteCityTextViewGenerator {

    Context context;
    DatabaseHelper dbHelper;
    ArrayAdapter<City> cityAdapter;

    /**
     * Constructor.
     *
     * @param context  The context in which the AutoCompleteTextView is to be used.
     * @param dbHelper An instance of a DatabaseHelper. This object is used to make the database
     *                 queries.
     */
    public AutoCompleteCityTextViewGenerator(Context context, DatabaseHelper dbHelper) {
        this.context = context;
        this.dbHelper = dbHelper;
    }

    /**
     * @param editField    The component to "transform" into one that shows a city drop down list
     *                     based on the current input. Make sure to pass an initialized object,
     *                     else a java.lang.NullPointerException will be thrown.
     * @param listLimit    Determines how many items shall be shown in the drop down list at most.
     * @param selectedCity The City object that will be passed as this parameter will be assigned
     *                     to the city that was selected from the drop down list.
     */
    public void getInstance(AutoCompleteTextView editField, int listLimit, City selectedCity) {
        cityAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, new ArrayList<City>());
        editField.setAdapter(cityAdapter);
        editField.addTextChangedListener(new TextChangeListener(editField, listLimit, selectedCity));
        editField.setOnItemClickListener(new ItemClickListener(editField, selectedCity));
    }

    /**
     * The following listener implementation provides the functionality / logic for the lookahead
     * dropdown.
     */
    private class TextChangeListener implements TextWatcher {

        private AutoCompleteTextView editField;
        private int dropdownListLimit;
        private City selectedCity;

        private TextChangeListener(AutoCompleteTextView editField, int dropdownListLimit, City selectedCity) {
            this.editField = editField;
            this.dropdownListLimit = dropdownListLimit;
            this.selectedCity = selectedCity;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            selectedCity = null;
            if (dbHelper != null) {
                String content = editField.getText().toString();
                if (content.length() > 3) {
                    // Get the matched cities
                    List<City> cities = dbHelper.getCitiesWhereNameLike(content, dropdownListLimit);
                    // Set the drop down entries
                    cityAdapter.clear();
                    cityAdapter.addAll(cities);
                    editField.showDropDown();
                } else {
                    editField.dismissDropDown();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

    }

    /**
     * This class handles the click on items of the dropdown menu. It sets the selected city and
     * hides the keyboard.
     */
    private class ItemClickListener implements AdapterView.OnItemClickListener {

        private AutoCompleteTextView editField;
        private City selectedCity;

        private ItemClickListener(AutoCompleteTextView editField, City selectedCity) {
            this.editField = editField;
            this.selectedCity = selectedCity;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectedCity = (City) parent.getItemAtPosition(position);

            InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }

    }

}
