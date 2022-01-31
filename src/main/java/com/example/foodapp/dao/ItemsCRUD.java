package com.example.foodapp.dao;

import org.springframework.data.repository.CrudRepository;

public interface ItemsCRUD extends CrudRepository<Items,Integer> {
    Iterable<Items> findByNameLike(String searchString);
}
