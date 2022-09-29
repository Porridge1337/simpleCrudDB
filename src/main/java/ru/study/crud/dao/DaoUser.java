package ru.study.crud.dao;

import ru.study.crud.model.Users;

import java.util.List;
import java.util.Optional;

public interface DaoUser {

    List<Users> findAll();

    Optional<Users> findById(int id);

    boolean save(Users user);

    boolean update(Users user);

    boolean deleteById(int o);
}
