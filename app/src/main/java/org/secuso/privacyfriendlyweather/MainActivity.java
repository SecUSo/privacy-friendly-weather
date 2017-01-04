package org.secuso.privacyfriendlyweather;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends BaseActivity {

    private final String DEBUG_TAG = "main_activity_debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(0, 0);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list_view_cities);

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
