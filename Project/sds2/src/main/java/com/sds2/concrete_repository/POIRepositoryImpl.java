package com.sds2.concrete_repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.sds2.classes.POI;
import com.sds2.database.Database;
import com.sds2.repository.POIRepository;

@Repository
public class POIRepositoryImpl implements POIRepository {

    private final Database database;

    public POIRepositoryImpl(Database database) {
        this.database = database;
    }

    @Override
    public POI findById(Long id) {
        String query = "SELECT id, name, description, price, currency, latitude, longitude, type, country " +
                       "FROM pois WHERE id = ?";
        try {
            Connection connection = database.getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return mapResultSetToPOI(resultSet);
            }
        }
    }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
    }

    @Override
    public void addPOI(POI poi) {
        String query = "INSERT INTO pois (name, type, description, price, currency, pictures, bookingLink, duration, city) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection connection = database.getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, poi.getCityName());
                preparedStatement.setString(2, poi.getDescription());
                preparedStatement.setDouble(3, poi.getPrice().getAmount());
                preparedStatement.setString(4, poi.getPrice().getCurrency());
                preparedStatement.setString(5, poi.getType());
                preparedStatement.setString(6, poi.getCountry());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<POI> findByCity(String city) {
        String query = "SELECT id, name, type, description, price, currency, pictures, bookingLink, duration, city " +
                       "FROM pois WHERE name = ?";
        try {
            Connection connection = database.getConnection();

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, city);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    List<POI> pois = new ArrayList<>();
                    while (resultSet.next()) {
                        pois.add(mapResultSetToPOI(resultSet));
                    }
                    return pois;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private POI mapResultSetToPOI(ResultSet resultSet) throws SQLException {
        POI poi = new POI();
        poi.setId(resultSet.getLong("id"));
        poi.setCityName(resultSet.getString("name"));
        poi.setType(resultSet.getString("type"));
        poi.setDescription(resultSet.getString("description"));
        poi.setPrice(resultSet.getDouble("price"), resultSet.getString("currency"));
        poi.setPictures(resultSet.getString("pictures"));
        poi.setBookingLink(resultSet.getString("bookingLink"));
        poi.setDuration(resultSet.getInt("duration"));
        poi.setCountry(resultSet.getString("city"));
        return poi;
    }
}