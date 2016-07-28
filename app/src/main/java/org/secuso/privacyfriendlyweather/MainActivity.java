package org.secuso.privacyfriendlyweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;

import org.secuso.privacyfriendlyweather.dialogs.DialogProvider;
import org.secuso.privacyfriendlyweather.preferences.PreferencesManager;

public class MainActivity extends BaseActivity {

    /**
     * Visual components.
     */
    FloatingActionButton fabAddLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(0, 0);

        // Object for access to app preferences
        SharedPreferences preferences = getSharedPreferences(PreferencesManager.PREFERENCES_NAME,
                Context.MODE_PRIVATE);
        PreferencesManager preferencesManager = new PreferencesManager(preferences);

        // Handle if app is started for the first time
        if (preferencesManager.isFirstAppStart()) {
            AlertDialog firstAppStartDialog = DialogProvider.getFirstAppStartDialog(this);
            firstAppStartDialog.show();
        }

        fabAddLocation = (FloatingActionButton) findViewById(R.id.fabAddLocation);
        handleFloatingButtonAddLocationClick(this);
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_example;
    }


    public static class WelcomeDialog extends DialogFragment {

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            LayoutInflater i = getActivity().getLayoutInflater();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle(getActivity().getString(R.string.welcome));
            builder.setPositiveButton(getActivity().getString(R.string.okay), null);
            builder.setNegativeButton(getActivity().getString(R.string.viewhelp), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((MainActivity) getActivity()).goToNavigationItem(R.id.nav_help);
                }
            });

            return builder.create();
        }
    }

    /**
     * When the floating button for adding a new location is clicked, open a dialog to do so.
     */
    private void handleFloatingButtonAddLocationClick(final Context context) {
        fabAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog addLocationDialog = DialogProvider.getAddLocationDialog(context);
                addLocationDialog.show();
            }
        });
    }

}
