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
import io.github.xpakx.micro.service.CommentService;
import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.entity.Comment;
import io.github.xpakx.micro.entity.UserRole;
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
import org.springframework.ui.Model;
import org.springframework.security.test.context.support.WithUserDetails;
import io.github.xpakx.micro.error.UserNotFound;
import io.github.xpakx.micro.error.UserUnauthorized;

import java.util.List;
import java.util.ArrayList;


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
  private PostService postService;
  
  @Mock
  private UserService userService;
  
  @Mock
  private CommentService commentService;
  
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
  
  Principal principal = new Principal() 
  {
    @Override
    public String getName() 
    {
      return "User";
    }
  };
  
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
    mockMvc
    
    //when
    .perform(post("/post/add").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1")
    .principal(principal))
    
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
  
  @Test
  public void shouldntAddEmptyPost() throws Exception 
  {     
    //given    
    User user = new User();
    Post post = new Post();
    given(postService.addPost(any(Post.class)))
    .willReturn(post);
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    mockMvc
    
    //when
    .perform(post("/post/add").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "")
    .principal(principal))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("addPost"))
    .andExpect(model().attributeHasFieldErrors("postForm", "message"));
      
    then(userService)
    .should(times(1))
    .findByUsername(anyString());
    then(userService).shouldHaveNoMoreInteractions();
    
    then(postService).shouldHaveZeroInteractions();
  }
  
  @Test
  public void shouldReturnPostById() throws Exception
  {
    //given
    Post post = new Post();
    post.setId(2);
    Page<Comment> comments = Page.empty();
    given(postService.findById(anyInt()))
    .willReturn(post);
    given(commentService.findAllByPostId(anyInt(), anyInt()))
    .willReturn(comments);
    mockMvc
    
    //when
    .perform(get("/post/2"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("post"))
    .andExpect(model().attributeExists("post"));
    then(postService)
    .should(times(1))
    .findById(2);
    then(userService).shouldHaveNoMoreInteractions();
  }
  
  @Test
  public void shouldDeletePostIfAuthorRequested() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    
    
    mockMvc
    
    //when
    .perform(get("/post/1/delete")
    .principal(principal))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/posts"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(postService)
    .should(times(1))
    .deletePost(1,1);
    then(postService).shouldHaveNoMoreInteractions();
  } 
  
  @Test
  public void shouldntDeletePostIfNotFound() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    willThrow(new UserNotFound("")).given(postService).deletePost(anyInt(), anyInt());
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    
    
    mockMvc
    
    //when
    .perform(get("/post/1/delete")
    .principal(principal))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("delete"))
    .andExpect(model().attributeExists("msg"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(postService)
    .should(times(1))
    .deletePost(1,1);
    then(postService).shouldHaveNoMoreInteractions();
  } 
  
  @Test
  public void shouldntDeletePostIfAnyoneRequested() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    
    willThrow(new UserUnauthorized(""))
    .given(postService).deletePost(anyInt(), anyInt());
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    
    
    mockMvc
    
    //when
    .perform(get("/post/1/delete")
    .principal(principal))
    
    //then
    .andExpect(status().isForbidden());
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(postService)
    .should(times(1))
    .deletePost(1,1);
    then(postService).shouldHaveNoMoreInteractions();
  } 
  
  @Test
  public void shouldDeletePostIfModRequested() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    UserRole role = new UserRole();
    role.setName("ROLE_MOD");
    List<UserRole> roles = new ArrayList<>();
    roles.add(role);
    user.setRoles(roles);
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    
    mockMvc
    
    //when
    .perform(get("/post/1/delete")
    .principal(principal))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/posts"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(postService)
    .should(times(1))
    .deletePost(1);
    then(postService).shouldHaveNoMoreInteractions();
  } 
  
  @Test
  public void shouldntDeletePostIfNotFoundMod() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User"); 
    UserRole role = new UserRole();
    role.setName("ROLE_MOD");
    List<UserRole> roles = new ArrayList<>();
    roles.add(role);
    user.setRoles(roles);
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    willThrow(new UserNotFound(""))
    .given(postService).deletePost(anyInt());
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    
    mockMvc
    
    //when
    .perform(get("/post/1/delete")
    .principal(principal))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("delete"))
    .andExpect(model().attributeExists("msg"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(postService)
    .should(times(1))
    .deletePost(1);
    then(postService).shouldHaveNoMoreInteractions();
  } 
  
  @Test
  public void shouldShowEditPostForm() throws Exception 
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    post.setMessage("msg1");
    given(postService.findById(anyInt()))
    .willReturn(post);
    
    mockMvc
    
    //when
    .perform(get("/post/1/edit"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("editPost"))
    .andExpect(model().attributeExists("post"));
    
    then(postService)
    .should(times(1))
    .findById(1);
    then(postService).shouldHaveNoMoreInteractions();
  }
  
  
  @Test
  public void shouldShowEditPostErrorIfPostNotFound() throws Exception 
  {
    //given
    given(postService.findById(anyInt()))
    .willReturn(null);
    
    mockMvc
    
    //when
    .perform(get("/post/1/edit"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("editPost"))
    .andExpect(model().attributeExists("err"))
    .andExpect(model().attributeExists("msg"));
    
    then(postService)
    .should(times(1))
    .findById(1);
    then(postService).shouldHaveNoMoreInteractions();
  }
  
  
  @Test
  public void shouldEditPostIfAuthorRequested() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    mockMvc
    
    //when
    .perform(post("/post/1/edit").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1")
    .principal(principal))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/posts"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
    ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> int2Captor = ArgumentCaptor.forClass(Integer.class);
    then(postService)
    .should(times(1))
    .updatePost(intCaptor.capture(), int2Captor.capture(), postCaptor.capture());
    then(postService).shouldHaveNoMoreInteractions();
 
    Post postArgument = postCaptor.getValue();
    assertThat(postArgument.getMessage(), 
      Matchers.is("msg1"));
    assertThat(intCaptor.getValue(), Matchers.is(1));
    assertThat(int2Captor.getValue(), Matchers.is(1));
  } 
  
  @Test
  public void shouldntEditPostIfNotFound() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    willThrow(new UserNotFound(""))
    .given(postService).updatePost(anyInt(), anyInt(), any(Post.class));
    mockMvc
    
    //when
    .perform(post("/post/1/edit").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1")
    .principal(principal))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("editPost"))
    .andExpect(model().attributeHasFieldErrors("postForm", "message"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(postService)
    .should(times(1))
    .updatePost(anyInt(), anyInt(), any(Post.class));
    then(postService).shouldHaveNoMoreInteractions();
  } 
  
  
  @Test
  public void shouldntEditPostIfNotFoundMod() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    UserRole role = new UserRole();
    role.setName("ROLE_MOD");
    List<UserRole> roles = new ArrayList<>();
    roles.add(role);
    user.setRoles(roles);
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    willThrow(new UserNotFound(""))
    .given(postService).updatePost(anyInt(), any(Post.class));
    mockMvc
    
    //when
    .perform(post("/post/1/edit").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1")
    .principal(principal))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("editPost"))
    .andExpect(model().attributeHasFieldErrors("postForm", "message"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(postService)
    .should(times(1))
    .updatePost(anyInt(), any(Post.class));
    then(postService).shouldHaveNoMoreInteractions();
  } 
  
  
  @Test
  public void shouldEditPostIfModRequested() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    UserRole role = new UserRole();
    role.setName("ROLE_MOD");
    List<UserRole> roles = new ArrayList<>();
    roles.add(role);
    user.setRoles(roles);
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    mockMvc
    
    //when
    .perform(post("/post/1/edit").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1")
    .principal(principal))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/posts"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
    ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);
    then(postService)
    .should(times(1))
    .updatePost(intCaptor.capture(), postCaptor.capture());
    then(postService).shouldHaveNoMoreInteractions();
 
    Post postArgument = postCaptor.getValue();
    assertThat(postArgument.getMessage(), 
      Matchers.is("msg1"));
    assertThat(intCaptor.getValue(), Matchers.is(1));
  } 
  
  @Test
  public void shouldntEditPostIfAnyoneRequested() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    
    willThrow(new UserUnauthorized(""))
    .given(postService).updatePost(anyInt(), anyInt(), any(Post.class));
    given(userService.findByUsername(anyString()))
    .willReturn(user);
    
    
    mockMvc
    
    //when
    .perform(post("/post/1/edit").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1")
    .principal(principal))
    
    //then
    .andExpect(status().isForbidden());
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(postService)
    .should(times(1))
    .updatePost(anyInt(), anyInt(), any(Post.class));
    then(postService).shouldHaveNoMoreInteractions();
  } 
}


