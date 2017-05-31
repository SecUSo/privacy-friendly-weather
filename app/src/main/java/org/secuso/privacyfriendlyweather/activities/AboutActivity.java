package org.secuso.privacyfriendlyweather.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.secuso.privacyfriendlyweather.BuildConfig;
import org.secuso.privacyfriendlyweather.R;

/**
 * Created by yonjuni on 15.06.16.
 */
public class AboutActivity extends BaseActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        overridePendingTransition(0, 0);


        //ActionBar ab = getSupportActionBar();
        //if(ab != null) {
        //    ab.setDisplayHomeAsUpEnabled(true);
        //}

        //View mainContent = findViewById(R.id.main_content);
        //if (mainContent != null) {
        //    mainContent.setAlpha(0);
        //    mainContent.animate().alpha(1).setDuration(BaseActivity.MAIN_CONTENT_FADEIN_DURATION);
        //}

        ((TextView)findViewById(R.id.secusoWebsite)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView)findViewById(R.id.githubURL)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView)findViewById(R.id.textFieldVersionName)).setText(BuildConfig.VERSION_NAME);

    }

    @Override
    protected int getNavigationDrawerID() {
        return R.id.nav_about;
    }
}

