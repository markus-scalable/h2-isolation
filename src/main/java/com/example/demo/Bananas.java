package com.example.demo;


import org.springframework.data.repository.Repository;

public interface Bananas extends Repository<DemoApplication.Banana, Integer> {

    boolean existsById(int id);

    void save(DemoApplication.Banana banana);

    void deleteAll();
}