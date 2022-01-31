package batch;

import com.example.foodapp.config.ItemsSaveJob;
import com.example.foodapp.dao.Items;
import com.example.foodapp.dto.ItemDTO;
import com.example.foodapp.processor.ItemsProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
public class ItemSaveJobTest {
    @Mock
    public JobBuilderFactory jobBuilderFactory;

    @Mock
    public StepBuilderFactory stepBuilderFactory;

    @Mock
    public DataSource dataSource;

    @InjectMocks
    public ItemsSaveJob itemSaveJob;


    @Test
    public void readerTest(){
        FlatFileItemReader<Items> reader = itemSaveJob.reader();
        Assert.assertNotNull(reader);
    }

    @Test
    public void processorTest() {
        Object process = itemSaveJob.processor();
        assert process instanceof ItemsProcessor;
    }

    @Test
    public void writerTest() {
        JdbcBatchItemWriter<ItemDTO> writer = itemSaveJob.writer();
        Assert.assertNotNull(writer);
    }

    @Test
    public void itemProcessTest() throws Exception {
        ItemsProcessor itemProcessor = new ItemsProcessor();
        Items item = new Items();
        item.setPrice(300);
        item.setDescription("Hello");
        item.setName("Test");
        item.setVendor_id(1);
        item.setId(1);
        ItemDTO itemDTO = itemProcessor.process(item);
        assert  itemDTO.getVendor_id().equals(item.getVendor_id());
        assert  itemDTO.getPrice().equals(item.getPrice());
        assert  itemDTO.getName().equals(item.getName());
    }
}
