package com.example.foodapp.dao;

import com.example.foodapp.dto.ItemDTO;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "items")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Items {
    @Id
    private Integer id;
    private Integer vendor_id;
    private String name;
    private String description;
    private Integer price;

    public ItemDTO getDTO(){
        ItemDTO item = new ItemDTO();
        item.setId(this.id);
        item.setVendor_id(this.vendor_id);
        item.setName(this.name);
        item.setDescription(this.description);
        item.setPrice(this.price);
        return item;
    }

}
