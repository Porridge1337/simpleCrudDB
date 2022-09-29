package ru.study.crud.dao.impl;

import ru.study.crud.dao.DaoRole;
import ru.study.crud.model.Role;

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

public class RoleDaoImpl implements DaoRole {

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
    public List<Role> findAll() {
        List<Role> roles = new CopyOnWriteArrayList<>();
        try (Connection conn = establishConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    """
                               SELECT * FROM role;
                            """
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Role role = new Role();
                role.setR_id(rs.getInt("r_id"));
                role.setRole(rs.getString("role"));
                roles.add(role);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }

    @Override
    public Optional<Role> findById(int id) {
        Role role = new Role();
        try (Connection conn = establishConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    """
                            SELECT * FROM role 
                            WHERE r_id = ?;
                            """
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                role.setR_id(rs.getInt("r_id"));
                role.setRole(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.of(role);
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
