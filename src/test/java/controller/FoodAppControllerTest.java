package controller;

import com.example.foodapp.config.JobLauncherConfig;
import com.example.foodapp.controller.BankService;
import com.example.foodapp.controller.FoodAppController;
import com.example.foodapp.dao.*;
import com.example.foodapp.dto.ItemDTO;
import com.example.foodapp.dto.OrderDTO;
import com.example.foodapp.dto.OrderRequest;
import com.example.foodapp.dto.Transaction;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;

import java.util.*;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
public class FoodAppControllerTest {

    @Mock
    JobLauncherConfig jobLauncherConfig;
    @Mock
    ItemsCRUD itemsCRUD;
    @Mock
    OrderCRUD orderCRUD;
    @Mock
    BankService bankService;
    @Mock
    CustomerCRUD customerCRUD;

    @InjectMocks
    FoodAppController foodAppController;

    @Test
    public void saveInfo() {
        String vendorJobStatus = foodAppController.persistInfo("Vendor");
        String itemsJobStatus = foodAppController.persistInfo("Items");
        assert vendorJobStatus.equals("Successfully saved info Vendor");
        assert itemsJobStatus.equals("Successfully saved info Items");
    }

    @Test
    public void saveInfoThrowsException() {
        given(jobLauncherConfig.lanchJobs("Items")).willThrow(new IllegalArgumentException("Job is already running"));
        String itemsJobStatus = foodAppController.persistInfo("Items");
        assert itemsJobStatus.equals("Failed to save Items");
    }

    @Test
    public void searchFoodItems() {
        List<Items> itemsList = new ArrayList();
        itemsList.add(new Items(1, 1, "Chiken Biryani", "Biryani" , 100));
        Items itemDAO = new Items();
        itemDAO.setId(2);
        itemDAO.setVendor_id(1);
        itemDAO.setName("Chiken 65");
        itemDAO.setDescription("Starter");
        itemDAO.setPrice(100);
        itemsList.add(itemDAO);
        given(itemsCRUD.findByNameLike("%chiken%")).willReturn(itemsList);
        List<ItemDTO> returnList = new ArrayList<>();
        for(Items item: itemsList){
            returnList.add(item.getDTO());
        }
        List<ItemDTO> response = (List<ItemDTO>) foodAppController.searchFoodItems("chiken");
        assert response.size() == returnList.size();
        assert response.get(0).getPrice().equals(returnList.get(0).getPrice());
        assert response.get(0).getId().equals(returnList.get(0).getId());
        assert response.get(0).getDescription().equals(returnList.get(0).getDescription());
        assert response.get(0).getName().equals(returnList.get(0).getName());
        assert response.get(0).getVendor_id().equals(returnList.get(0).getVendor_id());
    }

    @Test
    public void placeOrderWithEmptyAccount() throws Exception {
        OrderRequest or = new OrderRequest();
        ResponseEntity responseEntity  = foodAppController.placeOrder(or);
        assert responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }
    @Test
    public void placeOrderWithEmptyItemsList() throws Exception {
        OrderRequest or = new OrderRequest();
        or.setAccountNumber(1232343234l);
        ResponseEntity responseEntity = foodAppController.placeOrder(or);
        assert responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void placeOrder() throws Exception {
        OrderRequest order = new OrderRequest();
        order.setAccountNumber(123456l);
        order.setCustomerid(1);
        Map<Integer, Integer> items = new HashMap<>();
        items.put(1,1);
        order.setItemQuantityMap(items);
        Items item = new Items(1, 1, "Biryani", "Biryani", 300);
        given(itemsCRUD.findById(1)).willReturn(java.util.Optional.of(item));
        Transaction transaction = new Transaction();
        transaction.setFromAccountNumber(123456l);
        transaction.setToAccountNumber(692258139184l);
        transaction.setAmount(200.0);
        transaction.setComments("Food items order");
        Customer customer = new Customer();
        given(customerCRUD.findById(1)).willReturn(Optional.of(customer));
        given(bankService.initiateTransfer(Mockito.any(Transaction.class))).willReturn("Transaction successful");
        Order orderDAO = new Order();
        orderDAO.setOrderDate(new Date());
        orderDAO.setCustomerid(order.getCustomerid());
        orderDAO.setItemQuantityMap(order.getItemQuantityMap());
        given(orderCRUD.save(orderDAO)).willReturn(orderDAO);
        ResponseEntity responseEntity = foodAppController.placeOrder(order);
        OrderDTO response = (OrderDTO) responseEntity.getBody();
        assert response.getCustomerid().equals(orderDAO.getCustomerid());
        assert response.getItemQuantityMap().equals(orderDAO.getItemQuantityMap());
    }

    @Test
    public void placeOrderFails() throws Exception {
        OrderRequest order = new OrderRequest();
        order.setAccountNumber(123456l);
        Map<Integer, Integer> items = new HashMap<>();
        items.put(1,1);
        order.setItemQuantityMap(items);
        Items item = new Items(1, 1, "Biryani", "Biryani", 200);
        given(itemsCRUD.findById(1)).willReturn(java.util.Optional.of(item));
        Transaction transaction = new Transaction(123456l, 692258139184l, 200.0, "Food items order");
        given(bankService.initiateTransfer(transaction)).willReturn("Transaction failed");
        ResponseEntity responseEntity = foodAppController.placeOrder(order);
        assert responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
        assert responseEntity.getBody().equals("Please specify customer info");
    }

    @Test
    public void placeOrderThrowsException() throws Exception {
        OrderRequest order = new OrderRequest();
        order.setAccountNumber(123456l);
        order.setCustomerid(1);
        Map<Integer, Integer> items = new HashMap<>();
        items.put(1,1);
        order.setItemQuantityMap(items);
        Items item = new Items(1, 1, "Biryani", "Biryani", 200);
        given(itemsCRUD.findById(1)).willReturn(java.util.Optional.of(item));
        Transaction transaction = new Transaction(123456l, 692258139184l, 200.0, "Food items order");
        given(bankService.initiateTransfer(transaction)).willThrow(new RestClientException("Failed to communicate with service"));
        ResponseEntity responseEntity = foodAppController.placeOrder(order);
        assert responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST);
        assert responseEntity.getBody().equals("Customer is not present with provided ID");
    }

    @Test
    public void getOrderHistory() throws Exception {
        Order order = new Order();
        order.setOrderDate(new Date());
        order.setId(1);
        order.setCustomerid(1);
        Map<Integer, Integer> items = new HashMap<>();
        items.put(1,1);
        order.setItemQuantityMap(items);
        OrderDTO orderDTO = order.getPojo();
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        given(orderCRUD.findByCustomerid(1)).willReturn(orders);
        List<OrderDTO> orderDTOS = (List<OrderDTO>) foodAppController.getOrderHistory(1);
        OrderDTO response = orderDTOS.get(0);
        assert response.getOrderDate().equals(orderDTO.getOrderDate());
        assert response.getCustomerid().equals(orderDTO.getCustomerid());
        assert response.getItemQuantityMap().equals(orderDTO.getItemQuantityMap());
        assert response.getId().equals(orderDTO.getId());


    }
}
