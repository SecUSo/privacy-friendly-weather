package org.secuso.privacyfriendlyweather.files;

import org.secuso.privacyfriendlyweather.database.City;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides functionality for reading files.
 */
public class FileReader {

    /**
     * @param is The input stream of the file to read.
     * @return Returns a list of City instances.
     * @throws IOException In case the file cannot be read this exception will be thrown.
     */
    public List<City> readCitiesFromFile(InputStream is) throws IOException {
        List<City> cities = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        // Skip the first line as it contains headings
        String line = br.readLine();
        while ((line = br.readLine()) != null) {
            // id, name, lat, lon, country code
            String[] fields = line.split("\t");

            int id = Integer.parseInt(fields[0]);
            String name = fields[1];
            String countryCode = fields[2];
            String postalCode = fields[3];
            cities.add(new City(id, name, countryCode, postalCode));
        }
        br.close();
        return cities;
    }
}
