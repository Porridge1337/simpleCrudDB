package ru.study.crud.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, ID> {

    List<T> findAll();

    Optional<T> findById(int id);

    boolean save(T o);

    boolean update(T o);

    boolean deleteById(int o);
}
