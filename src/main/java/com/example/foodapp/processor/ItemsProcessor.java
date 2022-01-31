package com.example.foodapp.processor;

import com.example.foodapp.dao.Items;
import com.example.foodapp.dto.ItemDTO;
import org.springframework.batch.item.ItemProcessor;

public class ItemsProcessor implements ItemProcessor<Items, ItemDTO> {

    @Override
    public ItemDTO process(final Items itemDAO) throws Exception {

        final ItemDTO itemDTO = new ItemDTO(itemDAO.getId(), itemDAO.getVendor_id(), itemDAO.getName(), itemDAO.getDescription(), itemDAO.getPrice());
        return itemDTO;
    }

}
