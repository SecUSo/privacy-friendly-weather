package org.secuso.privacyfriendlyweather.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;

import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.database.data.City;
import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;
import org.secuso.privacyfriendlyweather.ui.util.AutoCompleteCityTextViewGenerator;
import org.secuso.privacyfriendlyweather.ui.util.MyConsumer;

/**
 * The configuration screen for the {@link WeatherWidget WeatherWidget} AppWidget.
 */
public class WeatherWidgetFiveDayForecastConfigureActivity extends Activity {

    private static final String PREFS_NAME = "org.secuso.privacyfriendlyweather.widget.WeatherWidget5Day";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private City selectedCity;

    AutoCompleteTextView mAppWidgetText;
    AutoCompleteCityTextViewGenerator generator;
    PFASQLiteHelper database;

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            handleOk();
        }
    };

    private void handleOk() {
        final Context context = WeatherWidgetFiveDayForecastConfigureActivity.this;

        if (selectedCity == null) {
            generator.getCityFromText(true);
            if (selectedCity == null) {
                return;
            }
        }

        //Task starts update Service with widget updates after insert is finished
        AddLocationWidgetTask addLocationWidgetTask = new AddLocationWidgetTask(getApplicationContext());
        addLocationWidgetTask.execute(selectedCity, mAppWidgetId, 5);

        // When the button is clicked, store the string locally
        saveTitlePref(context, mAppWidgetId, selectedCity.getCityId());

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }


    public WeatherWidgetFiveDayForecastConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, int cityId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_KEY + appWidgetId, cityId);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        return context.getString(R.string.appwidget_text);
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.weather_widget_configure);

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mAppWidgetText = (AutoCompleteTextView) findViewById(R.id.appwidget_text);

        database = PFASQLiteHelper.getInstance(this);
        generator = new AutoCompleteCityTextViewGenerator(getApplicationContext(), database);

        generator.generate(mAppWidgetText, 100, EditorInfo.IME_ACTION_DONE, new MyConsumer<City>() {
            @Override
            public void accept(City city) {
                selectedCity = city;
            }
        }, new Runnable() {
            @Override
            public void run() {
                handleOk();
            }
        });
    }
}

