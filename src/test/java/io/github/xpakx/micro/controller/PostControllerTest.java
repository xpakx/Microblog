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
import org.mockito.MockitoAnnotations;
import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.data.domain.Page;
import org.mockito.ArgumentCaptor;
import org.hamcrest.Matchers;
import java.security.Principal;

@RunWith(SpringRunner.class)
public class PostControllerTest 
{
  private WebApplicationContext context;

  @Autowired
  public void setWebApplicationContext(WebApplicationContext context) 
  {
    this.context = context;
  }

  private MockMvc mockMvc;
  
  
  @Mock
  private Principal principal;
    
  @Mock
  private PostService postService;
  
  @Mock
  private UserService userService;
  
  @InjectMocks
  private PostController postController;

  @BeforeEach
  public void setup() 
  {
    MockitoAnnotations.initMocks(this);
    
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources/templates/");
        viewResolver.setSuffix(".html");
        
    mockMvc = MockMvcBuilders
            .standaloneSetup(postController)
            .setViewResolvers(viewResolver)
            .build();
  }
  
  @Test
  public void controllerGetsPostsFromService() throws Exception 
  {
    //given
    Page<Post> posts = Page.empty();
    given(postService.findAll(anyInt()))
    .willReturn(posts);
    mockMvc
    
    //when
    .perform(get("/posts"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("posts"))
    .andExpect(model().attributeExists("posts"));
    
    then(postService)
    .should(times(1))
    .findAll(0);
    then(postService).shouldHaveNoMoreInteractions();
  }
 
  @Test
  public void controllerGetsPostsFromService2() throws Exception 
  {
    //given
    Page<Post> posts = Page.empty();
    given(postService.findAll(anyInt()))
    .willReturn(posts);
    mockMvc
    
    //when
    .perform(get("/all"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("posts"))
    .andExpect(model().attributeExists("posts"));
    then(postService)
    .should(times(1))
    .findAll(0);
    then(postService).shouldHaveNoMoreInteractions();
  }
  
  @Test
  public void controllerGetsPostsFromServiceByPage() throws Exception 
  {
    //given
    Page<Post> posts = Page.empty();
    given(postService.findAll(anyInt()))
    .willReturn(posts);
    mockMvc
    
    //when
    .perform(get("/posts/2"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("posts"))
    .andExpect(model().attributeExists("posts"));
    
    then(postService)
    .should(times(1))
    .findAll(2);
    then(postService).shouldHaveNoMoreInteractions();
  }
  
  @Test
  public void shouldShowPostForm() throws Exception 
  {
    //given
    mockMvc
    
    //when
    .perform(get("/post/add"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("addPost"))
    .andExpect(model().attributeExists("postForm"));
  }
  
  @Test
  public void shouldAddNewPost() throws Exception 
  {     
    //given    
    User user = new User();
    Post post = new Post();
    given(postService.addPost(any(Post.class)))
    .willReturn(post);
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    given(principal.getName())
    .willReturn("User");
    mockMvc
    
    //when
    .perform(post("/post/add").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1"))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/posts"));
      
    ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
    then(postService)
    .should(times(1))
    .addPost(postCaptor.capture());
    then(postService).shouldHaveNoMoreInteractions();
 
    Post postArgument = postCaptor.getValue();
    assertThat(postArgument.getMessage(), 
      Matchers.is("msg1"));
  }
  

}
