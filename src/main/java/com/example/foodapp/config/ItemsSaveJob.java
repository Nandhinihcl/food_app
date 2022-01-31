package com.example.foodapp.config;

import com.example.foodapp.dao.Items;
import com.example.foodapp.dto.ItemDTO;
import com.example.foodapp.processor.ItemsProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.batch.item.database.JdbcBatchItemWriter;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class ItemsSaveJob {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    @Value("${csv.file.location}")
    public String filePath;

    @Bean("itemReader")
    public FlatFileItemReader<Items> reader() {
        try{
            FlatFileItemReader<Items> reader = new FlatFileItemReader<Items>();
            reader.setResource(new FileSystemResource(filePath+"/items.csv"));
            reader.setLinesToSkip(1);
            reader.setLineMapper(new DefaultLineMapper<Items>() {{
                setLineTokenizer(new DelimitedLineTokenizer() {{
                    setNames(new String[] {"id","vendor_id","name","description","price" });
                }});
                setFieldSetMapper(new BeanWrapperFieldSetMapper() {{
                    setTargetType(Items.class);
                }});
            }});
            return reader;
        }catch (Exception e){
            System.out.println("Exception");
        }
        return null;
    }


    @Bean("itemsProcessor")
    public ItemsProcessor processor() {
        return new ItemsProcessor();
    }

    @Bean("itemWriter")
    public JdbcBatchItemWriter<ItemDTO> writer() {
        JdbcBatchItemWriter<ItemDTO> writer = new JdbcBatchItemWriter<ItemDTO>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("INSERT INTO items (id,vendor_id,name,description,price) " +
                "VALUES (:id,:vendor_id,:name,:description,:price)");
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean("itemsJob")
    public Job itemJob() {
        return jobBuilderFactory.get("itemsJob")
                .incrementer(new RunIdIncrementer())
                .flow(importData())
                .end()
                .build();
    }


    @Bean("itemJobStep")
    public Step importData() {
        return stepBuilderFactory.get("itemJobStep")
                .<Items, ItemDTO> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

}
