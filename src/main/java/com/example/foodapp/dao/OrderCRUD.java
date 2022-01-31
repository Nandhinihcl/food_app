package com.example.foodapp.dao;

import org.springframework.data.repository.CrudRepository;

public interface OrderCRUD extends CrudRepository<Order, Integer> {
    Iterable<Order> findByCustomerid(Integer customer_id);
}
