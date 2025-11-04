package com.sds2.concrete_repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sds2.classes.City;
import com.sds2.database.Database;
import com.sds2.repository.CityRepository;

public class CityRepositoryImpl implements CityRepository {

    private final Database database;

    public CityRepositoryImpl(Database database) {
        this.database = database;
    }

    @Override
    public City findByName(String name) {
        String query = "SELECT name, country, latitude, longitude FROM cities WHERE name = ?";
        try {
            Connection connection = database.getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return createCity(resultSet);
                    }
                }
            }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return null;
        }

    @Override
    public City findByCoordinates(double latitude, double longitude) {
        String query = "SELECT name, country, latitude, longitude FROM cities WHERE latitude = ? AND longitude = ?";
        try {
            Connection connection = database.getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setDouble(1, latitude);
                preparedStatement.setDouble(2, longitude);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return createCity(resultSet);
                    }
                }
            }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        return null;
    }

    public City createCity(ResultSet resultSet) throws SQLException {
        City city = new City();
        city.setName(resultSet.getString("name"));
        city.setCountry(resultSet.getString("country"));
        city.setCoordinates(
            resultSet.getDouble("latitude"), 
            resultSet.getDouble("longitude")
            );
        return city;
    }

    @Override
    public void addCity(City city) {
        String query = "INSERT INTO cities (name, country, latitude, longitude) VALUES (?, ?, ?, ?)";
        try {
            Connection connection = database.getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, city.getName());
                preparedStatement.setString(2, city.getCountry());
                preparedStatement.setDouble(3, city.getCoordinates()[0]);
                preparedStatement.setDouble(4, city.getCoordinates()[1]);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
