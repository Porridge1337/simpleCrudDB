package ru.study.crud.dao.impl;

import ru.study.crud.dao.Dao;
import ru.study.crud.model.Users;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserDaoImpl implements Dao<Users, String> {

    private static final Properties DB_SETTINGS = new Properties();

    static {
        String root = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().
                getResource("application.properties").getPath());
        try (InputStream inputStream = new FileInputStream(root)) {
            System.out.println(ClassLoader.getSystemClassLoader().getResourceAsStream("application.properties"));
            DB_SETTINGS.load(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Users> findAll() {
        List<Users> foundedUsers = new CopyOnWriteArrayList<>();
        try (Connection conn = establishConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    """
                               SELECT * FROM users;
                            """
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users user = new Users();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setAge(rs.getInt("age"));
                foundedUsers.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundedUsers;
    }

    @Override
    public Optional<Users> findById(int id) {
        Users userById = new Users();
        try (Connection conn = establishConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    """
                            SELECT * FROM users WHERE id = ?;
                            """
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userById.setId(rs.getInt("id"));
                userById.setName(rs.getString("name"));
                userById.setSurname(rs.getString("surname"));
                userById.setAge(rs.getInt("age"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(userById);
    }

    @Override
    public boolean save(Users o) {
        boolean resultSave = false;
        try (Connection connection = establishConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO users (name, surname, age) VALUES (?, ?, ?);
                            """);
            ps.setString(1, o.getName());
            ps.setString(2, o.getSurname());
            ps.setInt(3, o.getAge());
            resultSave = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSave;
    }

    @Override
    public boolean update(Users o) {
        boolean resultUpdate = false;
        try (Connection connection = establishConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            UPDATE users SET name = ?, surname = ?, age = ?
                            WHERE users.id = ?;
                            """);
            ps.setString(1, o.getName());
            ps.setString(2, o.getSurname());
            ps.setInt(3, o.getAge());
            ps.setInt(4, o.getId());
            resultUpdate = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultUpdate;
    }

    @Override
    public boolean deleteById(int o) {
        boolean resultDelete = false;
        try (Connection connection = establishConnection()) {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            DELETE FROM users WHERE users.id = ?;
                             """);
            ps.setInt(1, o);
            resultDelete = ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultDelete;
    }

    private Connection establishConnection() throws SQLException {
        try {
            Class.forName(DB_SETTINGS.getProperty("db.driverClassName")).getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Connection connection = DriverManager.getConnection(
                DB_SETTINGS.getProperty("db.url"),
                DB_SETTINGS.getProperty("db.user"),
                DB_SETTINGS.getProperty("db.password")

        );
        System.out.println(connection);
        return connection;
    }
}
