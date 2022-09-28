package ru.study.crud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    private int id;
    private String name;
    private String surname;
    private int age;
    List<Role> roleList = new ArrayList<>();

}
