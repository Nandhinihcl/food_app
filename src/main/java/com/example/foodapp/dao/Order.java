package com.example.foodapp.dao;

import com.example.foodapp.dto.OrderDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id
    @GeneratedValue
    private Integer id;
    @ElementCollection
    @CollectionTable(name = "order_item_mapping",
            joinColumns = {@JoinColumn(name = "order_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "item_id")
    @Column(name = "quantity")
    private Map<Integer, Integer> itemQuantityMap;
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;
    private Integer customerid;

    public OrderDTO getPojo(){
        OrderDTO order = new OrderDTO();
        order.setOrderDate(this.orderDate);
        order.setId(this.id);
        order.setCustomerid(this.customerid);
        order.setItemQuantityMap(this.itemQuantityMap);
        return order;
    }


}
