package io.github.xpakx.micro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.github.xpakx.micro.dialect.CustomDialect;

@SpringBootApplication
public class MicroApplication {

	 public static void main(String[] args) 
  {
		  SpringApplication.run(MicroApplication.class, args);
	 }

  /*@Bean
  public CustomDialect customDialect()
  {
    return new CustomDialect();
  }*/
}
