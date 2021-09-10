package com.cenit.eim.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication(exclude = { TaskSchedulingAutoConfiguration.class })
@EnableScheduling
public class OrchestratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrchestratorApplication.class, args);
	}

/* FIXME to property */
	@Bean(destroyMethod = "shutdown")
	public ExecutorService taskScheduler() {
		return Executors.newScheduledThreadPool(5);
	}
}
