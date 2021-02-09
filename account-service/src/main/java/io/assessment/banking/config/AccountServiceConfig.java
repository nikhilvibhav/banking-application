package io.assessment.banking.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for Account Service application
 *
 * @author Nikhil Vibhav
 */
@Configuration
public class AccountServiceConfig {

  /**
   * Generates a bean of {@link RestTemplate}
   *
   * @return {@link RestTemplate} bean
   */
  @Bean
  public RestTemplate getRestTemplate() {
    return new RestTemplateBuilder().build();
  }
}
