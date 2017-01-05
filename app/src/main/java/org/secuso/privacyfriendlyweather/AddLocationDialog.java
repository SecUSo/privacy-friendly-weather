package org.secuso.privacyfriendlyweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;

import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;

/**
 * Created by yonjuni on 04.01.17.
 */

public class AddLocationDialog extends DialogFragment {

    Activity activity;
    View rootView;
    PFASQLiteHelper database;

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

        //TODO AutoCompleteTextView
        AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTvAddDialog);

        builder.setPositiveButton(getActivity().getString(R.string.dialog_add_add_button), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                addCity();

            }
        });

        builder.setNegativeButton(getActivity().getString(R.string.dialog_add_close_button), null);

        return builder.create();
    }

    public void addCity() {


    }


}
