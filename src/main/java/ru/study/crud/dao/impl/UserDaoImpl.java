package ru.study.crud.dao.impl;

import ru.study.crud.dao.Dao;
import ru.study.crud.model.Role;
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
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
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
                               SELECT * FROM users AS u 
                               left join user_role as ur
                                on ur.user_id = u.id
                                left join role as r
                                on r.r_id = ur.role_id
                                ORDER BY r.role;
                            """
            );
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Users user = new Users();
                List<Role> roleList = new ArrayList<>();
                roleList.add(new Role(rs.getInt("r_id"), rs.getString("role")));
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setSurname(rs.getString("surname"));
                user.setAge(rs.getInt("age"));
                user.setRoleList(roleList);
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
                            SELECT u.id, u.name, u.surname, u.age, r.r_id, r.role FROM users AS u 
                               left join user_role as ur
                                on ur.user_id = u.id
                                left join role as r
                                on r.r_id = ur.role_id
                                WHERE u.id = ?;
                            """
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                List<Role> roleList = new ArrayList<>();
                userById.setId(rs.getInt("id"));
                userById.setName(rs.getString("name"));
                userById.setSurname(rs.getString("surname"));
                userById.setAge(rs.getInt("age"));
                roleList.add(new Role(rs.getInt("r_id"), rs.getString("role")));
                userById.setRoleList(roleList);
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
            PreparedStatement psUsers = connection.prepareStatement(
                    """
                            INSERT INTO users (name, surname, age) VALUES (?, ?, ?);
                            """, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement psUserRoles = connection.prepareStatement(
                    """
                            INSERT INTO user_role (user_id, role_id) VALUES (?, ?);
                            """
            );
            connection.setAutoCommit(false);
            Savepoint save1 = connection.setSavepoint();
            psUsers.setString(1, o.getName());
            psUsers.setString(2, o.getSurname());
            psUsers.setInt(3, o.getAge());
            if ((o.getName().equals("") && o.getName().length() > 30) ||
                    (o.getSurname().equals("") && o.getSurname().length() > 30) ||
                    o.getAge() < 0) {
                System.out.println("values shouldn't be null or less than zero");
                connection.rollback(save1);
            } else {
                int userUpdate = psUsers.executeUpdate();
                ResultSet generatedKeys = psUsers.getGeneratedKeys();
                if ((generatedKeys != null) && (generatedKeys.next())) {
                    int key = generatedKeys.getInt(1);
                    psUserRoles.setInt(2, o.getRoleList().get(0).getR_id());
                    psUserRoles.setInt(1, key);
                } else {
                    System.out.println("id не сгенерировался");
                    connection.rollback(save1);
                }
                int user_role = psUserRoles.executeUpdate();
                resultSave = userUpdate > 0 && user_role > 0;
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSave;
    }

    @Override
    public boolean update(Users o) {
        boolean resultUpdate = false;
        try (Connection connection = establishConnection()) {
            PreparedStatement psUser = connection.prepareStatement(
                    """
                            UPDATE users SET name = ?, surname = ?, age = ?
                            WHERE users.id = ?;
                            """);
            PreparedStatement psUserRole = connection.prepareStatement(
                    """
                            UPDATE user_role SET user_id = ?, role_id = ?
                            WHERE  user_role.user_id =?; 
                            """
            );
            connection.setAutoCommit(false);
            Savepoint save1 = connection.setSavepoint();

            psUser.setString(1, o.getName());
            psUser.setString(2, o.getSurname());
            psUser.setInt(3, o.getAge());
            psUser.setInt(4, o.getId());
            int userUpdate = psUser.executeUpdate();
            if ((o.getName().equals("") && o.getName().length() > 30) ||
                    (o.getSurname().equals("") && o.getSurname().length() > 30) ||
                    o.getAge() < 0) {
                System.out.println("values shouldn't be null or less than zero");
                connection.rollback(save1);
            } else {
                psUserRole.setInt(1, o.getId());
                psUserRole.setInt(2, o.getRoleList().get(0).getR_id());
                psUserRole.setInt(3, o.getId());
                int user_role = psUserRole.executeUpdate();
                resultUpdate = userUpdate > 0 && user_role > 0;
            }
            connection.commit();
            connection.setAutoCommit(true);
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
