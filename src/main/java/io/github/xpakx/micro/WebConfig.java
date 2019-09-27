package io.github.xpakx.micro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer
{   
 
  private MessageSource messageSource;
  
  @Autowired
  public void setMessageSource(MessageSource messageSource) 
  {
    this.messageSource = messageSource;
  }

  @Override
  public void addViewControllers(ViewControllerRegistry registry)
  {
    registry.addViewController("/login").setViewName("login");
    registry.addViewController("/admin").setViewName("admin");
    registry.addViewController("/").setViewName("main");
    registry.addViewController("/home").setViewName("main");
  }
 
  @Override
  public Validator getValidator() 
  {
    LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
    factory.setValidationMessageSource(messageSource);
    return factory;
  }
}
