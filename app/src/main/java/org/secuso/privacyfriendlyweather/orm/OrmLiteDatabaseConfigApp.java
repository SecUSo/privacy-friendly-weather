package org.secuso.privacyfriendlyweather.orm;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * This is a standalone program which is used to generate the ORMLite configuration file.
 */
public class OrmLiteDatabaseConfigApp extends OrmLiteConfigUtil {

    // All the classes to include in the config
    private static final Class<?>[] classes = new Class[]{
            City.class,
            CityToWatch.class,
            CurrentWeatherData.class,
            Forecast.class
    };

    public static void main(String[] args) throws IOException, SQLException {
        String currentDirectory = "user.dir";
        String configPath = "/app/src/main/res/raw/ormlite_config.txt";
        String projectRoot = System.getProperty(currentDirectory);
        String fullConfigPath = projectRoot + configPath;

        File configFile = new File(fullConfigPath);

        // Allow the config file to be generated more than once
        if (configFile.exists()) {
            configFile.delete();
            configFile = new File(fullConfigPath);
        }

        writeConfigFile(configFile, classes);
    }

}
