package org.secuso.privacyfriendlyweather;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import org.secuso.privacyfriendlyweather.database.PFASQLiteHelper;

/**
 * Created by yonjuni on 04.01.17.
 */

public class AddLocationDialog extends DialogFragment {

    Activity activity;
    View rootView;
    PFASQLiteHelper database;
    boolean closeDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = inflater.inflate(R.layout.dialog_add_metadata, null);

        rootView = view;

        builder.setView(view);
        builder.setIcon(R.mipmap.ic_launcher);
        //builder.setTitle(getActivity().getString(R.string.add_metadata_heading));

        this.database = new PFASQLiteHelper(getActivity());

        builder.setPositiveButton(getActivity().getString(R.string.dialog_add_add_button), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                //addCity();

            }
        });

        builder.setNegativeButton(getActivity().getString(R.string.dialog_add_close_button), null);

        return builder.create();
    }


}
