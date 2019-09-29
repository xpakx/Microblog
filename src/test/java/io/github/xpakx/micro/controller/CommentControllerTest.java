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

import io.github.xpakx.micro.service.CommentService;
import io.github.xpakx.micro.service.UserService;
import io.github.xpakx.micro.service.PostService;
import io.github.xpakx.micro.entity.Comment;
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
import org.hamcrest.Matchers;

import java.security.Principal;
import org.springframework.ui.Model;
import org.springframework.security.test.context.support.WithUserDetails;
import io.github.xpakx.micro.error.NotFoundException;
import io.github.xpakx.micro.error.UserUnauthorized;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@RunWith(SpringRunner.class)
public class CommentControllerTest 
{
  private WebApplicationContext context;

  @Autowired
  public void setWebApplicationContext(WebApplicationContext context) 
  {
    this.context = context;
  }

  private MockMvc mockMvc;
  
  @Mock
  private CommentService commentService;
  
  @Mock
  private PostService postService;
  
  @Mock
  private UserService userService;
  
  @InjectMocks
  private CommentController commentController;

  @BeforeEach
  public void setup() 
  {
    MockitoAnnotations.initMocks(this);
    
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/resources/templates/");
        viewResolver.setSuffix(".html");
        
    mockMvc = MockMvcBuilders
            .standaloneSetup(commentController)
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
  public void shouldShowCommentForm() throws Exception 
  {
    //given
    mockMvc
    
    //when
    .perform(get("/post/1/comment/add"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("addComment"))
    .andExpect(model().attributeExists("commentForm"));
  }
  
  @Test
  public void shouldAddNewComment() throws Exception 
  {     
    //given    
    User user = new User();
    Post post = new Post();
    post.setId(1);
    Comment comment = new Comment();
    comment.setPost(post);
    given(commentService.addComment(any(Comment.class)))
    .willReturn(comment);
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    given(postService.findById(anyInt()))
    .willReturn(post);
    mockMvc
    
    //when
    .perform(post("/post/1/comment/add").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1")
    .principal(principal))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/post/1"));
      
    ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
    then(commentService)
    .should(times(1))
    .addComment(commentCaptor.capture());
    then(commentService).shouldHaveNoMoreInteractions();
 
    Comment commentArgument = commentCaptor.getValue();
    assertThat(commentArgument.getMessage(), 
      Matchers.is("msg1"));
  }
  
  @Test
  public void shouldntAddEmptyComment() throws Exception 
  {     
    //given      
    User user = new User();
    Post post = new Post();
    post.setId(1);
    Comment comment = new Comment();
    comment.setPost(post);
    given(commentService.addComment(any(Comment.class)))
    .willReturn(comment);
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    mockMvc
    
    //when
    .perform(post("/post/1/comment/add").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "")
    .principal(principal))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("addComment"))
    .andExpect(model().attributeHasFieldErrors("commentForm", "message"));
      
    then(userService)
    .should(times(1))
    .findByUsername(anyString());
    then(userService).shouldHaveNoMoreInteractions();
    
    then(commentService).shouldHaveZeroInteractions();
  }
  
  @Test
  public void shouldDeleteCommentIfAuthorRequested() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    Comment comment = new Comment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    given(commentService.deleteComment(anyInt(), anyInt()))
    .willReturn(1);
    
    mockMvc
    
    //when
    .perform(post("/comment/1/delete").with(csrf())
    .principal(principal))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/post/1"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(commentService)
    .should(times(1))
    .deleteComment(1,1);
    then(commentService).shouldHaveNoMoreInteractions();
  } 
  
  @Test
  public void shouldntDeleteCommentIfNotFound() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    Comment comment = new Comment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setId(1);
    willThrow(new NotFoundException("")).given(commentService).deleteComment(anyInt(), anyInt());
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    
    
    mockMvc
    
    //when
    .perform(post("/comment/1/delete").with(csrf())
    .principal(principal))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("delete"))
    .andExpect(model().attributeExists("msg"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(commentService)
    .should(times(1))
    .deleteComment(1,1);
    then(commentService).shouldHaveNoMoreInteractions();
  } 
  
  @Test
  public void shouldntDeleteCommentIfAnyoneRequested() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    Comment comment = new Comment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setId(1);
    
    willThrow(new UserUnauthorized(""))
    .given(commentService).deleteComment(anyInt(), anyInt());
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    
    
    mockMvc
    
    //when
    .perform(post("/comment/1/delete").with(csrf())
    .principal(principal))
    
    //then
    .andExpect(status().isForbidden());
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(commentService)
    .should(times(1))
    .deleteComment(1,1);
    then(commentService).shouldHaveNoMoreInteractions();
  } 
  
  @Test
  public void shouldDeleteCommentIfModRequested() throws Exception
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
    Comment comment = new Comment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    given(commentService.deleteComment(anyInt()))
    .willReturn(1);
    
    mockMvc
    
    //when
    .perform(post("/comment/1/delete").with(csrf())
    .principal(principal))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/post/1"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(commentService)
    .should(times(1))
    .deleteComment(1);
    then(commentService).shouldHaveNoMoreInteractions();
  } 
  
  @Test
  public void shouldntDeleteCommentIfNotFoundMod() throws Exception
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
    Comment comment = new Comment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setId(1);
    willThrow(new NotFoundException(""))
    .given(commentService).deleteComment(anyInt());
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    
    mockMvc
    
    //when
    .perform(post("/comment/1/delete").with(csrf())
    .principal(principal))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("delete"))
    .andExpect(model().attributeExists("msg"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(commentService)
    .should(times(1))
    .deleteComment(1);
    then(commentService).shouldHaveNoMoreInteractions();
  } 
  
  @Test
  public void shouldShowEditCommentForm() throws Exception 
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    post.setMessage("msg1");
    Comment comment = new Comment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setId(1);
    given(commentService.findById(anyInt()))
    .willReturn(comment);
    
    mockMvc
    
    //when
    .perform(get("/comment/1/edit"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("editComment"))
    .andExpect(model().attributeExists("comment"));
    
    then(commentService)
    .should(times(1))
    .findById(1);
    then(commentService).shouldHaveNoMoreInteractions();
  }
  
  
  @Test
  public void shouldShowCommentPostErrorIfPostNotFound() throws Exception 
  {
    //given
    given(commentService.findById(anyInt()))
    .willReturn(null);
    
    mockMvc
    
    //when
    .perform(get("/comment/1/edit"))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("editComment"))
    .andExpect(model().attributeExists("err"))
    .andExpect(model().attributeExists("msg"));
    
    then(commentService)
    .should(times(1))
    .findById(1);
    then(commentService).shouldHaveNoMoreInteractions();
  }
  
  
  @Test
  public void shouldEditCommentIfAuthorRequested() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    Comment comment = new Comment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    given(commentService.updateComment(anyInt(), anyInt(), any(Comment.class)))
    .willReturn(1);
    mockMvc
    
    //when
    .perform(post("/comment/1/edit").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1")
    .principal(principal))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/post/1"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
    ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<Integer> int2Captor = ArgumentCaptor.forClass(Integer.class);
    then(commentService)
    .should(times(1))
    .updateComment(intCaptor.capture(), int2Captor.capture(), commentCaptor.capture());
    then(commentService).shouldHaveNoMoreInteractions();
 
    Comment commentArgument = commentCaptor.getValue();
    assertThat(commentArgument.getMessage(), 
      Matchers.is("msg1"));
    assertThat(intCaptor.getValue(), Matchers.is(1));
    assertThat(int2Captor.getValue(), Matchers.is(1));
  } 
  
  @Test
  public void shouldntEditCommentIfNotFound() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    Comment comment = new Comment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    willThrow(new NotFoundException(""))
    .given(commentService).updateComment(anyInt(), anyInt(), any(Comment.class));
    mockMvc
    
    //when
    .perform(post("/comment/1/edit").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1")
    .principal(principal))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("editComment"))
    .andExpect(model().attributeHasFieldErrors("commentForm", "message"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(commentService)
    .should(times(1))
    .updateComment(anyInt(), anyInt(), any(Comment.class));
    then(commentService).shouldHaveNoMoreInteractions();
  } 
  
  
  @Test
  public void shouldntEditCommentIfNotFoundMod() throws Exception
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
    Comment comment = new Comment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    willThrow(new NotFoundException(""))
    .given(commentService).updateComment(anyInt(), any(Comment.class));
    mockMvc
    
    //when
    .perform(post("/comment/1/edit").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1")
    .principal(principal))
    
    //then
    .andExpect(status().isOk())
    .andExpect(view().name("editComment"))
    .andExpect(model().attributeHasFieldErrors("commentForm", "message"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    then(commentService)
    .should(times(1))
    .updateComment(anyInt(), any(Comment.class));
    then(commentService).shouldHaveNoMoreInteractions();
  } 
  
  
  @Test
  public void shouldEditCommentIfModRequested() throws Exception
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
    Comment comment = new Comment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setId(1);
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    given(commentService.updateComment(anyInt(), any(Comment.class)))
    .willReturn(1);
    mockMvc
    
    //when
    .perform(post("/comment/1/edit").with(csrf())
    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
    .param("id", "1")
    .param("message", "msg1")
    .principal(principal))
    
    //then
    .andExpect(status().isFound())
    .andExpect(redirectedUrl("/post/1"));
    
    then(userService)
    .should(times(1))
    .findByUsername("User");
    then(userService).shouldHaveNoMoreInteractions();
    
    ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
    ArgumentCaptor<Integer> intCaptor = ArgumentCaptor.forClass(Integer.class);
    then(commentService)
    .should(times(1))
    .updateComment(intCaptor.capture(), commentCaptor.capture());
    then(commentService).shouldHaveNoMoreInteractions();
 
    Comment commentArgument = commentCaptor.getValue();
    assertThat(commentArgument.getMessage(), 
      Matchers.is("msg1"));
    assertThat(intCaptor.getValue(), Matchers.is(1));
  } 
  
  @Test
  public void shouldntEditCommentIfAnyoneRequested() throws Exception
  {
    //given
    User user = new User();
    user.setId(1);
    user.setUsername("User");
    Post post = new Post();
    post.setUser(user);
    post.setId(1);
    Comment comment = new Comment();
    comment.setPost(post);
    comment.setUser(user);
    comment.setId(1);
    willThrow(new UserUnauthorized(""))
    .given(commentService).updateComment(anyInt(), anyInt(), any(Comment.class));
    given(userService.findByUsername(anyString()))
    .willReturn(Optional.of(user));
    
    
    mockMvc
    
    //when
    .perform(post("/comment/1/edit").with(csrf())
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
    
    then(commentService)
    .should(times(1))
    .updateComment(anyInt(), anyInt(), any(Comment.class));
    then(commentService).shouldHaveNoMoreInteractions();
  } 
}


