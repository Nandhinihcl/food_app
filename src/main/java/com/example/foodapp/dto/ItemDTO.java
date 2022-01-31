package com.example.foodapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ItemDTO {
    private Integer id;
    private Integer vendor_id;
    private String name;
    private String description;
    private Integer price;
}
