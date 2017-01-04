package org.secuso.privacyfriendlyweather.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.secuso.privacyfriendlyweather.AddLocationDialog;
import org.secuso.privacyfriendlyweather.R;

public class MainActivity extends BaseActivity {

    private final String DEBUG_TAG = "main_activity_debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0, 0);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_view_cities);

        FloatingActionButton addFab = (FloatingActionButton) findViewById(R.id.fabAddLocation);
        if (addFab != null) {

            addFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    AddLocationDialog addMetaDataDialog = new AddLocationDialog();
                    addMetaDataDialog.show(fragmentManager, "AddLocationDialog");
                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_manage;
    }

}
