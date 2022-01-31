package batch;

import com.example.foodapp.config.VendorSaveJob;
import com.example.foodapp.dao.Items;
import com.example.foodapp.dao.Vendor;
import com.example.foodapp.dto.ItemDTO;
import com.example.foodapp.dto.VendorDTO;
import com.example.foodapp.processor.ItemsProcessor;
import com.example.foodapp.processor.VendorProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)

public class VendorSaveJobTest {
    @Mock
    public JobBuilderFactory jobBuilderFactory;

    @Mock
    public StepBuilderFactory stepBuilderFactory;

    @Mock
    public DataSource dataSource;

    @InjectMocks
    public VendorSaveJob vendorSaveJob;

    @Test
    public void readerTest(){
        FlatFileItemReader<Vendor> reader = vendorSaveJob.reader();
        Assert.assertNotNull(reader);
    }

    @Test
    public void processorTest() {
        Object process = vendorSaveJob.processor();
        assert process instanceof VendorProcessor;
    }

    @Test
    public void writerTest() {
        JdbcBatchItemWriter<VendorDTO> writer = vendorSaveJob.writer();
        Assert.assertNotNull(writer);
    }

    @Test
    public void vendorProcessTest() throws Exception {
        VendorProcessor vendorProcessor = new VendorProcessor();
        Vendor vendor = new Vendor();
        vendor.setId(1);
        vendor.setName("Test");
        vendor.setDescription("Hello");
        VendorDTO vendorDTO = vendorProcessor.process(vendor);
        assert  vendorDTO.getId().equals(vendor.getId());
        assert  vendorDTO.getDescription().equals(vendor.getDescription());
        assert  vendorDTO.getName().equals(vendor.getName());
    }
   /* @Test
    public void vendorJob() {
        JobBuilder jobBuilder = mock(JobBuilder.class);
        StepBuilder stepBuilder = mock(StepBuilder.class);
        SimpleStepBuilder simpleStepBuilder = mock(SimpleStepBuilder.class);
        TaskletStep taskletStep = mock(TaskletStep.class);

        given(jobBuilderFactory.get("vendorJob")).willReturn(jobBuilder);
        given(stepBuilderFactory.get("VendorJobStep")).willReturn(stepBuilder);
        given(stepBuilder.chunk(10)).willReturn(simpleStepBuilder);
        given(simpleStepBuilder.reader(Mockito.any(ItemReader.class))).willReturn(simpleStepBuilder);
        given(simpleStepBuilder.processor(Mockito.any(ItemProcessor.class))).willReturn(simpleStepBuilder);
        given(simpleStepBuilder.writer(Mockito.any(ItemWriter.class))).willReturn(simpleStepBuilder);
        given(simpleStepBuilder.build()).willReturn(taskletStep);
        Job job = vendorSaveJob.vendorJob();
        Assert.assertNotNull(job);
    }*/




}
