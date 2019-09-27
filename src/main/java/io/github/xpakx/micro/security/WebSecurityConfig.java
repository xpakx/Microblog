package io.github.xpakx.micro.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, proxyTargetClass = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter 
{
  private UserDetailsService customUserDetailsService;
  
  @Autowired
  public void setUserDetailsService(UserDetailsService customUserDetailsService) 
  {
    this.customUserDetailsService = customUserDetailsService;
  }
 
  @Bean
  public PasswordEncoder passwordEncoder() 
  {
    return new BCryptPasswordEncoder();
  }
 
  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception 
  {
    auth
    .userDetailsService(customUserDetailsService)
    .passwordEncoder(passwordEncoder());
  }
 
  @Override
  protected void configure(HttpSecurity http) throws Exception 
  {
    http         
    .headers()
      .frameOptions().sameOrigin()
      .and()
    .authorizeRequests()
      .antMatchers("/resources/**", "/webjars/**","/assets/**").permitAll()
      .antMatchers("/", "/posts", "/posts/**", "/all", "/register").permitAll()
      .antMatchers("/admin/**").hasRole("ADMIN")
      .anyRequest().authenticated()
      .and()
    .formLogin()
      .loginPage("/login")
      .defaultSuccessUrl("/home")
      .failureUrl("/login?error")
      .permitAll()
      .and()
    .logout()
      .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
      .logoutSuccessUrl("/login?logout")
      .permitAll()
      .and()
    .exceptionHandling();
  }
}
