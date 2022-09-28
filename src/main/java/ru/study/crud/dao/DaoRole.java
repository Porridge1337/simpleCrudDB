package ru.study.crud.dao;

import ru.study.crud.model.Role;

import java.util.List;
import java.util.Optional;

public interface DaoRole {

    List<Role> findAll();
    Optional<Role> findById(int id);
}
