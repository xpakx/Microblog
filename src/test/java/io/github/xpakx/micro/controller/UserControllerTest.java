package io.github.xpakx.micro.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.mockito.Mock;
import org.mockito.InjectMocks;

import io.github.xpakx.micro.service.PostService;
import io.github.xpakx.micro.service.UserService;
import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.entity.UserRole;
import org.mockito.MockitoAnnotations;
import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.data.domain.Page;
import org.mockito.ArgumentCaptor;
import static org.hamcrest.Matchers.*;

import java.security.Principal;
import org.springframework.ui.Model;
import org.springframework.security.test.context.support.WithUserDetails;
import io.github.xpakx.micro.error.UserNotFound;
import io.github.xpakx.micro.error.UserUnauthorized;

import java.util.List;
import java.util.ArrayList;

import static org.mockito.BDDMockito.any;


@RunWith(SpringRunner.class)
public class UserControllerTest 
{
  private WebApplicationContext context;

  @Autowired
  public void setWebApplicationContext(WebApplicationContext context) 
  {
    this.context = context;
  }

  private MockMvc mockMvc;
  
  @Mock
  private PostService postService;
  
  @Mock
  private UserService userService;
  
  @InjectMocks
  private UserController userController;

  @BeforeEach
  public void setup() 
  {
    MockitoAnnotations.initMocks(this);
    
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources/templates/");
        viewResolver.setSuffix(".html");
        
    mockMvc = MockMvcBuilders
            .standaloneSetup(userController)
            .setViewResolvers(viewResolver)
            .build();
  }
  
  Principal principal = new Principal() 
  {
    @Override
    public String getName() 
    {
      return "User";
    }
  };
    
  @Test
  public void shouldShowRegisterForm() throws Exception 
  {
    //given
    mockMvc
    
    //when
    .perform(get("/register"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("register"))
    .andExpect(model().attributeExists("userForm"));
  }
  
  @Test
  public void shouldAddNewUser() throws Exception 
  {     
    //given    
    given(userService.findByEmail(anyString()))
    .willReturn(null);
    given(userService.findByUsername(anyString()))
    .willReturn(null);
    mockMvc
    
    //when
    .perform(post("/register").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("username", "Test")
    .param("email", "test@example.com")
    .param("password", "test")
    .param("confirmPassword", "test"))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/login"));
      
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    
    then(userService)
    .should(times(1))
    .findByEmail(anyString());
    then(userService)
    .should(times(1))
    .findByUsername(anyString());
    then(userService)
    .should(times(1))
    .save(userCaptor.capture());
 
    User userArgument = userCaptor.getValue();
    assertThat(userArgument.getUsername(), is("Test"));
    assertThat(userArgument.getEmail(), is("test@example.com"));
    assertThat(userArgument.getPassword(), is("test"));
    assertNull(userArgument.getId());
  }
  
  @Test
  public void shouldntAddNewUserIfUsernameUsed() throws Exception 
  {     
    //given    
    User user = new User();
    given(userService.findByEmail(anyString()))
    .willReturn(null);
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    mockMvc
    
    //when
    .perform(post("/register").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("username", "Test")
    .param("email", "test@example.com")
    .param("password", "test")
    .param("confirmPassword", "test"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("register"))
    .andExpect(model().attributeExists("msg"));
      
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    
    then(userService)
    .should(never())
    .save(any(User.class));
  }
  
  @Test
  public void shouldntAddNewUserIfEmailUsed() throws Exception 
  {     
    //given    
    User user = new User();
    given(userService.findByEmail(anyString()))
    .willReturn(user);
    given(userService.findByUsername(anyString()))
    .willReturn(null);
    mockMvc
    
    //when
    .perform(post("/register").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("username", "Test")
    .param("email", "test@example.com")
    .param("password", "test")
    .param("confirmPassword", "test"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("register"))
    .andExpect(model().attributeExists("msg"));
      
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    
    then(userService)
    .should(never())
    .save(any(User.class));
  }
  
  @Test
  public void shouldntAddNewUserIfPasswordsDiff() throws Exception 
  {     
    //given    
    given(userService.findByEmail(anyString()))
    .willReturn(null);
    given(userService.findByUsername(anyString()))
    .willReturn(null);
    mockMvc
    
    //when
    .perform(post("/register").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("username", "Test")
    .param("email", "test@example.com")
    .param("password", "test")
    .param("confirmPassword", "test2"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("register"))
    .andExpect(model().attributeExists("msg"));
      
    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
    
    then(userService)
    .should(never())
    .save(any(User.class));
  }
  
}


