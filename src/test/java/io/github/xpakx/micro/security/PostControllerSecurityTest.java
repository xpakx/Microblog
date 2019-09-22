package io.github.xpakx.micro.security;

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
import io.github.xpakx.micro.controller.PostController;
import io.github.xpakx.micro.service.UserService;
import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.User;
import org.mockito.MockitoAnnotations;
import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;
import org.mockito.ArgumentCaptor;
import org.hamcrest.Matchers;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PostControllerSecurityTest 
{
  private WebApplicationContext context;

  @Autowired
  public void setWebApplicationContext(WebApplicationContext context) 
  {
    this.context = context;
  }

  private MockMvc mockMvc;
  
  @BeforeEach
  public void setup() 
  {
    MockitoAnnotations.initMocks(this);
    mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
  }
  
  @Test
  public void postNewPostUnavailableForAll() throws Exception 
  {
    //given
    mockMvc
    
    //when
    .perform(post("/post/add").with(csrf())
    .param("id", "1")
    .param("message", "msg1"))
    
    //then
    .andExpect(status().is3xxRedirection())
    .andExpect(redirectedUrlPattern("**/login"));
  }
  
  @Test
  @WithMockUser(roles="USER", username="User")
  public void shouldntAddEmptyPost() throws Exception 
  {     
    //given    
    mockMvc
    
    //when
    .perform(post("/post/add").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", ""))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("addPost"))
    .andExpect(model().attributeExists("msg"));
  }
  
  @Test
  @WithMockUser(roles="USER", username="User")
  public void shouldAddNewPost() throws Exception 
  {     
    //given    
    mockMvc
    
    //when
    .perform(post("/post/add").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1"))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/posts"));
  }

}
