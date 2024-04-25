package org.residentme.workorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * The main application class.
 */
@SpringBootApplication
@EnableAutoConfiguration(exclude= {DataSourceAutoConfiguration.class })
@EnableMongoRepositories(basePackages = {"org.residentme.workorder.repository"})
@EntityScan(basePackages = {"org.residentme.workorder.entity"})
@EnableScheduling
public class WKApplication {

  /**
   * The task executor.
   * @return The task executor.
   */
  @Bean(name = "taskExecutor")
  public SimpleAsyncTaskExecutor getExecutorMadgrades(){
    return new SimpleAsyncTaskExecutor  ();
  }
  
  public static void main(String[] args) {
	  SpringApplication.run(WKApplication.class, args);
  }
}
