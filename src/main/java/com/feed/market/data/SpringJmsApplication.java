package com.feed.market.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ WebConfig.class })
public class SpringJmsApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringJmsApplication.class, args);
  }
}
