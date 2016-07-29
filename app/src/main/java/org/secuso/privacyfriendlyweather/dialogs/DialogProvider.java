package org.secuso.privacyfriendlyweather.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.secuso.privacyfriendlyweather.HelpActivity;
import org.secuso.privacyfriendlyweather.R;
import org.secuso.privacyfriendlyweather.orm.DatabaseHelper;
import org.secuso.privacyfriendlyweather.pojos.City;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class provides predefined dialogs.
 */
public class DialogProvider {

    /**
     * Visual components
     */
    private LinearLayout addDialogLinearLayout;
    private TextView addDialogTvMessage;
    private AutoCompleteTextView addDialogEdtLocation;
    private CheckBox addDialogCbSave;

    private boolean isAddLocationDialogInitialized = false;
    private DatabaseHelper dbHelper;
    private Dao<City, Integer> cityDao;

    /**
     * Constructor.
     */
    public DialogProvider() {
    }

    /**
     * Constructor.
     *
     * @param dbHelper The database helper is used to provide further features like look-ahead. If
     *                 no DatabaseHelper is provided, these features are not available.
     */
    public DialogProvider(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        cityDao = this.dbHelper.getCityDao();
    }

    /**
     * Initializes the linear layout as well as its components that is used for the
     * AddLocationDialog.
     *
     * @param context The context in which the dialog is to be displayed.
     */
    private void initLayoutForAddLocationDialog(final Context context) {
        if (!isAddLocationDialogInitialized) {
            final float scale = context.getResources().getDisplayMetrics().density;
            int padding = 10;
            int paddingInPD = (int) (padding * scale + 0.5f);

            addDialogLinearLayout = new LinearLayout(context);
            addDialogLinearLayout.setOrientation(LinearLayout.VERTICAL);
            addDialogLinearLayout.setPadding(paddingInPD, paddingInPD, paddingInPD, paddingInPD);

            addDialogTvMessage = new TextView(context);
            addDialogTvMessage.setTextAppearance(context,
                    android.R.style.TextAppearance_DeviceDefault_Medium);
            addDialogTvMessage.setText("Enter the location to add:");

            addDialogEdtLocation = new AutoCompleteTextView(context);
            addDialogEdtLocation.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (dbHelper != null) {
                        String content = addDialogEdtLocation.getText().toString();
                        if (content.length() > 2) {
                            QueryBuilder<City, Integer> queryBuilder = cityDao.queryBuilder();
                            try {
                                queryBuilder.where().like("city_name", String.format("%s%%", content));
                                queryBuilder.limit((long) 5);
                                PreparedQuery<City> preparedQuery = queryBuilder.prepare();
                                Iterator<City> iterator = cityDao.query(preparedQuery).iterator();
                                List<String> matchedCities = new ArrayList<String>();
                                while (iterator.hasNext()) {
                                    City nextCity = iterator.next();
                                    matchedCities.add(
                                            String.format("%s, %s", nextCity.getCityName(), nextCity.getCountryCode())
                                    );
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                                        android.R.layout.simple_list_item_1, matchedCities);
                                addDialogEdtLocation.setAdapter(adapter);
                                addDialogEdtLocation.showDropDown();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            // TODO: Show keyboard by default

            addDialogCbSave = new CheckBox(context);
            addDialogCbSave.setText("Save this location");

            addDialogLinearLayout.addView(addDialogTvMessage);
            addDialogLinearLayout.addView(addDialogEdtLocation);
            addDialogLinearLayout.addView(addDialogCbSave);
        }
    }

    /**
     * @param context The context in which the dialog is to be displayed.
     * @return Returns an AlertDialog object with an info text for the first app start and two
     * buttons (1. Go to help page, 2. Close dialog).
     */
    public AlertDialog getFirstAppStartDialog(final Context context) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setMessage(R.string.dialog_first_app_start_message);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setNegativeButton(R.string.dialog_first_app_help_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Activity activity = (Activity) context;
                Fragment fragment = new HelpActivity.HelpFragment();
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_content, fragment).addToBackStack(null).commit();
            }
        });
        dialogBuilder.setPositiveButton(R.string.dialog_first_app_close_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return dialogBuilder.create();
    }

    /**
     * @param context The context in which the dialog is to be displayed.
     * @return Returns an AlertDialog with a text input field for a location and a CheckBox for
     * saving this location.
     */
    public AlertDialog getAddLocationDialog(final Context context) {
        initLayoutForAddLocationDialog(context);
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setCancelable(false);
        dialogBuilder.setView(addDialogLinearLayout);

        // Buttons are added but not their onClick implementation; this is done further below
        // as explained here (as of 2016-07-27):
        // http://stackoverflow.com/questions/6275677/alert-dialog-in-android-should-not-dismiss
        dialogBuilder.setNegativeButton(R.string.dialog_add_add_button, null);
        dialogBuilder.setPositiveButton(R.string.dialog_add_close_button, null);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnClose = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button btnAdd = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                // Close click event
                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                // Add click event
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String trimmedInput = addDialogEdtLocation.getText().toString().trim();
                        if (trimmedInput.length() > 0) {
                            String location = trimmedInput;
                            boolean store = addDialogCbSave.isChecked();
                            dialog.dismiss();
                        } else {
                            addDialogEdtLocation.setError("Please enter a valid location!");
                        }
                    }
                });
            }
        });

        return dialog;
    }

}
