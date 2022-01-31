package com.example.foodapp.dao;

import com.example.foodapp.dto.VendorDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "vendor")
@Getter @Setter
public class Vendor {
    @Id
    private Integer id;
    private String name;
    private String description;

    public VendorDTO getDTO() {
        VendorDTO vendorDTO = new VendorDTO();
        vendorDTO.setId(this.id);
        vendorDTO.setName(this.name);
        vendorDTO.setDescription(this.description);
        return vendorDTO;
    }
}
