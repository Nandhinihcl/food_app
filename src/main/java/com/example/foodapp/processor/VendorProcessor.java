package com.example.foodapp.processor;

import com.example.foodapp.dao.Vendor;
import com.example.foodapp.dto.VendorDTO;
import org.springframework.batch.item.ItemProcessor;


public class VendorProcessor implements ItemProcessor<Vendor, VendorDTO> {

    @Override
    public VendorDTO process(final Vendor vendorModel) throws Exception {

        final VendorDTO vendorDTO = new VendorDTO(vendorModel.getId(), vendorModel.getName(), vendorModel.getDescription());

        return vendorDTO;
    }

}
