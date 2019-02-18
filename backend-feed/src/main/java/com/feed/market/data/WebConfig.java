package com.feed.market.data;


import java.time.LocalDate;
import java.util.List;

import javax.servlet.Filter;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 */

@Configuration
@EnableSwagger2
//public class hWebConfig extends WebMvcConfigurationSupport {
public class WebConfig extends WebMvcConfigurationSupport {
	
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
    
    
//    @Bean
//	  public MappingJackson2HttpMessageConverter customJackson2HttpMessageConverter() {
//
//	    MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
//	    ObjectMapper objectMapper = new ObjectMapper();
//	    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
//	    objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
//	    jsonConverter.setObjectMapper(objectMapper);
//	    return jsonConverter;
//	  }

	  @Override
	  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

//	    converters.add(customJackson2HttpMessageConverter());
	    super.addDefaultHttpMessageConverters(converters);
	  }

	  //

	  @Override
	  public void addResourceHandlers(final ResourceHandlerRegistry registry) {

	    registry
	        .addResourceHandler("swagger-ui.html")
	        .addResourceLocations("classpath:/META-INF/resources/");
	    registry
	        .addResourceHandler("/webjars/**")
	        .addResourceLocations("classpath:/META-INF/resources/webjars/");
	    registry
	        .addResourceHandler("/**")
	        .addResourceLocations("classpath:/META-INF/resources/index.html");
	    registry
	        .addResourceHandler("/static/**")
	        .addResourceLocations("classpath:/META-INF/resources/static/");
	    registry
	        .addResourceHandler("/")
	        .addResourceLocations("classpath:/META-INF/resources/index.html");
	  }

	  @Bean
	  public FilterRegistrationBean<Filter> someFilterRegistration() {

	    final FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
	    registration.setFilter(etagFilter());
	    registration.addUrlPatterns("/*");
	    registration.setName("etagFilter");
	    registration.setOrder(1);
	    return registration;
	  }

	  @Bean(name = "etagFilter")
	  public Filter etagFilter() {

	    return new ShallowEtagHeaderFilter();
	  }

}