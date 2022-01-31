package com.example.foodapp.controller;

import com.example.foodapp.config.JobLauncherConfig;
import com.example.foodapp.dao.*;
import com.example.foodapp.dto.ItemDTO;
import com.example.foodapp.dto.OrderDTO;
import com.example.foodapp.dto.OrderRequest;
import com.example.foodapp.dto.Transaction;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping(path = "/food-app")
@OpenAPIDefinition(info = @Info(title = "Food App",
        description = "API for food order",
        version = "2.0"
))
@RequiredArgsConstructor
public class FoodAppController {

    private final JobLauncherConfig jobLauncherConfig;
    private final ItemsCRUD itemsCRUD;
    private final OrderCRUD orderCRUD;
    private final BankService bankService;
    private final CustomerCRUD customerCRUD;

    @PostMapping(path = "/saveInfo")
    public @ResponseBody
    String persistInfo(@RequestParam String jobName){
        try {
            jobLauncherConfig.lanchJobs(jobName);
        }catch (Exception e){
            System.out.println(e);
            return "Failed to save "+jobName;
        }
        return "Successfully saved info "+jobName;
    }

    @PostMapping(path="/searchFoodItems")
    public @ResponseBody Iterable<ItemDTO> searchFoodItems(@RequestParam String itemName){
        Iterable<Items> items = null;
        try{
            Integer foodId = Integer.parseInt(itemName);
            Optional<Items> foodItems = itemsCRUD.findById(foodId);
            if(foodItems == null || !foodItems.isPresent()) return null;
            items = new ArrayList<>(); ((ArrayList<Items>) items).add(foodItems.get());
        }catch (NumberFormatException numberFormatException){
            items = itemsCRUD.findByNameLike("%"+itemName+"%");
            if(items == null) return null;
        }
        List<ItemDTO> responseList = new ArrayList<>();
        for(Items item: items){
            responseList.add(item.getDTO());
        }
        return responseList;
    }

    @PostMapping(path = "/place-order")
    public @ResponseBody
    ResponseEntity placeOrder(@RequestBody OrderRequest request) throws Exception{
        if(request.getAccountNumber() == null) {
            System.out.println("Account number is empty");
            return ResponseEntity.badRequest().body("Account number is empty");
        }else if(request.getItemQuantityMap() == null || request.getItemQuantityMap().size() == 0){
            return ResponseEntity.badRequest().body("Please place at-least one item");
        }else if(request.getCustomerid() == null){
            return ResponseEntity.badRequest().body("Please specify customer info");
        }
        Optional<Customer> customer = customerCRUD.findById(request.getCustomerid());
        if(!customer.isPresent()) return ResponseEntity.badRequest().body("Customer is not present with provided ID");
        Double amount = 0.0;
        Map<Integer, Integer> items = request.getItemQuantityMap();
        for(Integer item: items.keySet()){
            Optional<Items> foodItem = itemsCRUD.findById(item);
            amount += foodItem.isPresent()? foodItem.get().getPrice()*items.get(item): 0;
        }
        if(amount<250) return ResponseEntity.badRequest().body("Minimum order to place is 250");
        String result = initiateFundTransfer((long)request.getAccountNumber(), 692258139184l, amount, "Food items order" );
        if("Transaction successful".equals(result)){
            Order order = new Order();
            order.setOrderDate(new Date());
            order.setCustomerid(request.getCustomerid());
            order.setItemQuantityMap(request.getItemQuantityMap());
            orderCRUD.save(order);
            return ResponseEntity.ok().body(order.getPojo());
        }else{
            return ResponseEntity.badRequest().body(result);
        }
    }

    private String initiateFundTransfer(Long fromAccNumber, Long toAccNumber, Double amount, String comments){
        try {
            Transaction transaction = new Transaction(fromAccNumber, toAccNumber, amount, comments);
            String result = bankService.initiateTransfer(transaction);
            return  result;
        } catch (Exception exception) {
            System.out.println("Exception raised");
            return "Exception Raised";
        }
    }
    @GetMapping(path = "/order-history")
    public @ResponseBody Iterable<OrderDTO> getOrderHistory(@RequestParam Integer customerId) {
        Iterable<Order> ordersList = orderCRUD.findByCustomerid(customerId);
        List<OrderDTO> orders = new ArrayList();
        for(Order order: ordersList){
            orders.add(order.getPojo());
        }
        return orders;
    }



}
