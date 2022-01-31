package com.example.foodapp.config;

import com.example.foodapp.dao.Vendor;
import com.example.foodapp.dto.VendorDTO;
import com.example.foodapp.processor.VendorProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import javax.sql.DataSource;


@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class VendorSaveJob {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Value("${csv.file.location}")
    public String filePath;

    @Bean("vendorReader")
    public FlatFileItemReader<Vendor> reader() {
        FlatFileItemReader<Vendor> reader = new FlatFileItemReader<Vendor>();
        reader.setResource(new FileSystemResource(filePath+"/vendors.csv"));
        reader.setLinesToSkip(1);
        reader.setLineMapper(new DefaultLineMapper<Vendor>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "id","name","description"});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper() {{
                setTargetType(Vendor.class);
            }});
        }});
        return reader;
    }


    @Bean("vendorProcessor")
    public VendorProcessor processor() {
        return new VendorProcessor();
    }

    @Bean("vendorWriter")
    public JdbcBatchItemWriter<VendorDTO> writer() {
        JdbcBatchItemWriter<VendorDTO> writer = new JdbcBatchItemWriter<VendorDTO>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO Vendor (id,name,description) " +
                "VALUES (:id,:name,:description)");
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean("vendorJob")
    public Job vendorJob() {
        return jobBuilderFactory.get("vendorJob")
                .incrementer(new RunIdIncrementer())
                .flow(importData())
                .end()
                .build();
    }

    @Bean("VendorJobStep")
    public Step importData() {
        return stepBuilderFactory.get("VendorJobStep")
                .<Vendor, VendorDTO> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

}
