package batch;

import com.example.foodapp.config.ItemsSaveJob;
import com.example.foodapp.config.JobLauncherConfig;
import lombok.SneakyThrows;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
public class JobLaunchedConfigTest {

    @Mock
    JobLauncher jobLauncher;

    @Mock
    @Qualifier("vendorJob")
    Job vendorJob;

    @Mock
    @Qualifier("itemsJob")
    Job itemsJob;

    @InjectMocks
    public JobLauncherConfig jobLauncherConfig;

    @SneakyThrows
    @Test
    public void launchJobsVendorTest() {
        JobExecution execution = mock(JobExecution.class);
       given(jobLauncher.run(eq(vendorJob), Mockito.any(JobParameters.class))).willReturn(execution);
       given(execution.getExitStatus()).willReturn(ExitStatus.COMPLETED);
       String result = jobLauncherConfig.lanchJobs("Vendor");
       assert result.equals("Vendor job execution completed, status : COMPLETED");
    }

    @SneakyThrows
    @Test
    public void launchJobsItemsTest() {
        JobExecution execution = mock(JobExecution.class);
        given(jobLauncher.run(eq(itemsJob), Mockito.any(JobParameters.class))).willReturn(execution);
        given(execution.getExitStatus()).willReturn(ExitStatus.COMPLETED);
        String result = jobLauncherConfig.lanchJobs("Items");
        assert result.equals("Items job execution completed, status : COMPLETED");
    }


}
