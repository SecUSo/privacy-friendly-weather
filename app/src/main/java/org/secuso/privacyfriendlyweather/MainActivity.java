package org.secuso.privacyfriendlyweather;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.secuso.privacyfriendlyweather.dialogs.DialogProvider;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.pojos.City;
import org.secuso.privacyfriendlyweather.preferences.PreferencesManager;

import java.sql.SQLException;
import java.util.Iterator;

public class MainActivity extends BaseActivity {

    /**
     * Visual components.
     */
    private FloatingActionButton fabAddLocation;

    private DatabaseHelper dbHelper;
    private DialogProvider dialogProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(0, 0);

        dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        dialogProvider = new DialogProvider(dbHelper);

        // Object for access to app preferences
        SharedPreferences preferences = getSharedPreferences(PreferencesManager.PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        PreferencesManager preferencesManager = new PreferencesManager(preferences);

        // Handle if app is started for the first time
        if (preferencesManager.isFirstAppStart()) {
            AlertDialog firstAppStartDialog = dialogProvider.getFirstAppStartDialog(this);
            firstAppStartDialog.show();
        }

        fabAddLocation = (FloatingActionButton) findViewById(R.id.fabAddLocation);
        handleFloatingButtonAddLocationClick(this);
    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null) {
            dbHelper.close();
        }

        super.onDestroy();
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_example;
    }

    /**
     * When the floating button for adding a new location is clicked, open a dialog to do so.
     */
    private void handleFloatingButtonAddLocationClick(final Context context) {
        fabAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog addLocationDialog = dialogProvider.getAddLocationDialog(context);
                addLocationDialog.show();
            }
        });
    }

}
