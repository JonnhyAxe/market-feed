package com.feed.market.data;


import java.time.LocalDate;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 */

@Configuration
@ComponentScan({ "com.codenotfound" })
@EnableSwagger2
//public class hWebConfig extends WebMvcConfigurationSupport {
public class WebConfig {
	
	@Value("${activemq.broker-url}")
	private String brokerUrl;

	@Bean // Enabling and configuring Swagger
	public Docket mainConfig() { // @formatter:off
		//http://localhost:8080/swagger-ui.html
	    return new Docket(DocumentationType.SWAGGER_2)
	            .select().apis(RequestHandlerSelectors.any())
	            .paths(PathSelectors.any())
	            .build()
	            .pathMapping("/")
	            .directModelSubstitute(LocalDate.class, String.class)
	            .genericModelSubstitutes(ResponseEntity.class); // The model
	                                                            // data rather
	                                                            // Spring
	                                                            // specific
	                                                            // artifacts
	}// @formatter:on
    
    
    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
      ActiveMQConnectionFactory activeMQConnectionFactory =
          new ActiveMQConnectionFactory();
      activeMQConnectionFactory.setBrokerURL(brokerUrl);

      return activeMQConnectionFactory;
    }

}
