package org.secuso.privacyfriendlyweather.preferences;

import android.content.SharedPreferences;

/**
 * This class provides access and methods for relevant preferences.
 */
public class PreferencesManager {

    /**
     * Constants
     */
    public static final String PREFERENCES_NAME = "org.secuso.privacyfriendlyweather.preferences";
    private static final String PREFERENCES_IS_FIRST_START = "is_first_app_start";

    /**
     * Member variables
     */
    SharedPreferences preferences;

    /**
     * Constructor.
     *
     * @param preferences Source for the preferences to use.
     */
    public PreferencesManager(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    private void setFirstAppStartToFalse() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREFERENCES_IS_FIRST_START, false);
        editor.apply();
    }

    /**
     * @return Returns true if the app is started for the very first time else false.
     */
    public boolean isFirstAppStart() {
        boolean isFirstStart = preferences.getBoolean(PREFERENCES_IS_FIRST_START, true);
        if (isFirstStart) {
            setFirstAppStartToFalse();
        }
        return isFirstStart;
    }

}
