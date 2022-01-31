package com.example.foodapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class OrderDTO {
  private Integer id;
  private Map<Integer, Integer> itemQuantityMap;
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date orderDate;
  private Integer customerid;

}
