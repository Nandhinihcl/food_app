package com.example.foodapp.config;

import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableBatchProcessing
public class JobLauncherConfig {


    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("vendorJob")
    Job vendorJob;

    @Autowired
    @Qualifier("itemsJob")
    Job itemsJob;

    public String lanchJobs(String jobName) {

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters parameters = new JobParameters(maps);

        try {

            if("Vendor".equals(jobName)) {
                JobExecution vendor = jobLauncher.run(vendorJob, parameters);
                String status = String.format("Vendor job execution completed, status : %s", vendor.getExitStatus().getExitCode());
                System.out.println(status);
                return status;
            }

            else if("Items".equals(jobName)) {
                JobExecution items = jobLauncher.run(itemsJob, parameters);
                String status = String.format("Items job execution completed, status : %s", items.getExitStatus().getExitCode());
                System.out.println(status);
                return status;
            }

        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException exception) {

            System.out.println(exception.getMessage());
        }
        return "";
    }
}
