package org.secuso.privacyfriendlyweather;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.secuso.privacyfriendlyweather.dialogs.DialogProvider;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.preferences.PreferencesManager;
import org.secuso.privacyfriendlyweather.services.ServiceReceiver;
import org.secuso.privacyfriendlyweather.services.CreateDatabaseService;
import org.secuso.privacyfriendlyweather.ui.DataUpdater;
import org.secuso.privacyfriendlyweather.weather_api.OwmHttpRequestForUpdatingCityList;

import java.sql.SQLException;

public class MainActivity extends BaseActivity {

    private final String DEBUG_TAG = "main_activity_debug";

    /**
     * Visual components.
     */
    private FloatingActionButton fabAddLocation;

    private DatabaseHelper dbHelper;
    private DialogProvider dialogProvider;
    private ServiceReceiver createDatabaseReceiver;
    private ProgressDialog progressAddDialog;
    private AlertDialog addLocationDialog;
    // It is safer to initialize this to true;
    private boolean canOpenAddDialog = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0, 0);

        dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        dialogProvider = new DialogProvider(dbHelper);
        progressAddDialog = new ProgressDialog(MainActivity.this);
        addLocationDialog = dialogProvider.getAddLocationDialog(this);
        fabAddLocation = (FloatingActionButton) findViewById(R.id.fabAddLocation);
        handleFloatingButtonAddLocationClick(this);

        // Object for access to app preferences
        SharedPreferences preferences = getSharedPreferences(PreferencesManager.PREFERENCES_NAME, Context.MODE_PRIVATE);
        PreferencesManager preferencesManager = new PreferencesManager(preferences);

        // Handle if app is started for the first time
        if (preferencesManager.isFirstAppStart()) {
            canOpenAddDialog = false;
            // Start the background service to create the database and import the cities
            setupServiceReceiver();
            launchCreateDatabaseService();

            AlertDialog firstAppStartDialog = dialogProvider.getFirstAppStartDialog(this);
            firstAppStartDialog.show();
        }
        // App was used before
        else {
            // It might be that the app was not closed properly (e. g. using a task manager); in that
            // case the cities_to_watch table was not cleaned, so we do it now as well
            try {
                dbHelper.deleteNonPersistentCitiesToWatch();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // Update the cities list
            DataUpdater dataUpdater = new DataUpdater(dbHelper);
            dataUpdater.updateCurrentWeatherData(new OwmHttpRequestForUpdatingCityList(this, dbHelper));
        }
    }

    @Override
    protected void onDestroy() {
        // Clear the database and show a message of how many locations were deleted
        try {
            int numberOfDeletions = dbHelper.deleteNonPersistentCitiesToWatch();
            if (numberOfDeletions > 0) {
                final String DELETION_MSG_TEMPLATE = (numberOfDeletions == 1) ?
                        getResources().getString(R.string.action_destroy_deletion_number_singular) :
                        getResources().getString(R.string.action_destroy_deletion_number_plural);
                String deletionMsg = String.format(DELETION_MSG_TEMPLATE, numberOfDeletions);
                Toast.makeText(this, deletionMsg, Toast.LENGTH_LONG).show();
            }
        } catch (SQLException e) {
            final String ERROR_MSG = getResources().getString(R.string.action_destroy_error_on_deletion);
            Toast.makeText(this, ERROR_MSG, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        // Close the database connection
        dbHelper.close();
        // If this line is removed, the app crashes when the app is reopened as the onCreate method
        // would try to re-open a new DB helper
        // (see http://stackoverflow.com/questions/12770092/attempt-to-re-open-an-already-closed-object-sqlitedatabase)
        OpenHelperManager.releaseHelper();

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
                // This code is executed only once when the database is setup (first run); when the
                // database is setup, the receiver will close this dialog and show the add location
                // dialog
                if (!canOpenAddDialog) {
                    progressAddDialog.setTitle(getResources().getString(R.string.progress_dialog_add_location_title));
                    progressAddDialog.setMessage(getResources().getString(R.string.progress_dialog_add_location_msg));
                    progressAddDialog.setCancelable(false);
                    progressAddDialog.show();

                }
                // This code is executed each time after first run
                else {
                    addLocationDialog.show();
                }
            }
        });
    }

    /**
     * Launches the service that prepares the database.
     */
    private void launchCreateDatabaseService() {
        Intent intent = new Intent(this, CreateDatabaseService.class);
        intent.putExtra("receiver", createDatabaseReceiver);
        startService(intent);
    }

    /**
     * Setup the callback when the service is done.
     */
    public void setupServiceReceiver() {
        createDatabaseReceiver = new ServiceReceiver(new Handler());
        // This is where we specify what happens when data is received from the service
        createDatabaseReceiver.setReceiver(new ServiceReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                // Next time the floating action button is clicked the add location dialog will
                // appear
                canOpenAddDialog = true;

                progressAddDialog.dismiss();
                addLocationDialog.show();
            }
        });
    }

}
