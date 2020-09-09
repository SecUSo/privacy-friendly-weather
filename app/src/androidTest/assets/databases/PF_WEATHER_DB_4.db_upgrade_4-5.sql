DROP TABLE CITIES;
CREATE TABLE CITIES (
            cities_id INTEGER PRIMARY KEY,
            city_name VARCHAR(100) NOT NULL,
            country_code VARCHAR(10) NOT NULL,
            longitude REAL NOT NULL,
            latitude REAL NOT NULL);