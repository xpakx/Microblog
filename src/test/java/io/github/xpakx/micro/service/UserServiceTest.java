package io.github.xpakx.micro.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.xpakx.micro.repository.UserRepository;
import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.error.UserNotFound;
import io.github.xpakx.micro.error.UserUnauthorized;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;
import org.mockito.MockitoAnnotations;
import static org.hamcrest.Matchers.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.ArgumentCaptor;


import java.util.List;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
public class UserServiceTest 
{

  @Mock
  private UserRepository userRepository;
  
  @InjectMocks
  private UserService userService;

  @BeforeEach
  public void setup() 
  {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void shouldReturnUserById() throws Exception 
  {
    //given
    User user = new User();
    given(userRepository.findById(anyInt()))
    .willReturn(Optional.of(user));
    
    //when
    User result = userService.findById(1);
    
    //then
    then(userRepository)
    .should(times(1))
    .findById(1);
    then(userRepository).shouldHaveNoMoreInteractions();
    assertNotNull(result);
    assertThat(result, is(user));
  }
  
  @Test
  public void shouldReturnUserByUsername() throws Exception 
  {
    //given
    User user = new User();
    given(userRepository.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    
    //when
    User result = userService.findByUsername("User");
    
    //then
    then(userRepository)
    .should(times(1))
    .findByUsername("User");
    then(userRepository).shouldHaveNoMoreInteractions();
    assertNotNull(result);
    assertThat(result, is(user));
  }
  
  @Test
  public void shouldReturnNullUserById() throws Exception 
  {
    //given
    User user = new User();
    given(userRepository.findById(anyInt()))
    .willReturn(Optional.empty());
    
    //when
    User result = userService.findById(1);
    
    //then
    then(userRepository)
    .should(times(1))
    .findById(1);
    then(userRepository).shouldHaveNoMoreInteractions();
    assertNull(result);
  }
  
  @Test
  public void shouldReturnNullUserByUsername() throws Exception 
  {
    //given
    User user = new User();
    given(userRepository.findByUsername(anyString()))
    .willReturn(Optional.empty());
    
    //when
    User result = userService.findByUsername("User");
    
    //then
    then(userRepository)
    .should(times(1))
    .findByUsername("User");
    then(userRepository).shouldHaveNoMoreInteractions();
    assertNull(result);
  }
  
  @Test
  public void shouldAddUserToRepository() throws Exception 
  {
    //given
    User user = new User();
    user.setPassword("abc");
    user.setUsername("User");
    
    //when
    userService.save(user);
    
    //then
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    then(userRepository)
    .should(times(1))
    .save(userCaptor.capture());
    then(userRepository).shouldHaveNoMoreInteractions();
    
    User userArgument = userCaptor.getValue();
    assertThat(userArgument.getRoles(), hasSize(1));
    assertThat(userArgument.getRoles().get(0).getName(), is("ROLE_USER"));
    assertThat(userArgument.getPassword(), not(is("abc")));
    
  }
  
}