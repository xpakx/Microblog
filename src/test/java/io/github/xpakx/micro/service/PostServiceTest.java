package io.github.xpakx.micro.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.xpakx.micro.repository.PostRepository;
import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.User;
import io.github.xpakx.micro.error.NotFoundException;
import io.github.xpakx.micro.error.UserUnauthorized;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;
import org.mockito.MockitoAnnotations;
import org.hamcrest.Matchers;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.ArgumentCaptor;


import java.util.List;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
public class PostServiceTest 
{

  @Mock
  private PostRepository postRepository;
  
  @InjectMocks
  private PostService postService;
  
  Post first;
  Post second;
  Post third;
  User user;

  @BeforeEach
  public void setup() 
  {
    MockitoAnnotations.initMocks(this);
    initPosts();
  }
  
  private void initPosts()
  {
    user = new User();
    user.setId(1);
    user.setUsername("User"); 
    user.setEmail("user@example.com");
    first = new Post();
    first.setId(1); 
    first.setMessage("msg1");
    first.setUser(user);
    second = new Post();
    second.setId(2); 
    second.setMessage("msg2");
    second.setUser(user);
    third = new Post();
    third.setId(3); 
    third.setMessage("msg3");
    third.setUser(user);
  }

  @Test
  public void serviceShouldReturnPostIfPostFound() throws Exception 
  {
    //given
    given(postRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
    Post result = postService.findById(1);
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository).shouldHaveNoMoreInteractions();
    assertNotNull(result);
    assertThat(result, Matchers.is(first));
  }
  
  @Test
  public void serviceShouldReturnNullIfPostNotFound() throws Exception
  {
    //given
    given(postRepository.findById(anyInt()))
    .willReturn(Optional.empty());
    
    //when
    Post result = postService.findById(1);
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository).shouldHaveNoMoreInteractions();
    assertNull(result);
  }
  
  @Test
  public void serviceShouldDeletePostIfFound() throws Exception
  {
    //given
    given(postRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
    postService.deletePost(1);
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository)
    .should(times(1))
    .delete(first);
    then(postRepository).shouldHaveNoMoreInteractions();
  }
  
  @Test
  public void serviceShouldThrowExceptionWhenTryToDeletePostNotFound()
  {
    //given
    given(postRepository.findById(anyInt()))
    .willReturn(Optional.empty());
    
    //???
    assertThrows(NotFoundException.class, () ->
      postService.deletePost(1));
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository).shouldHaveNoMoreInteractions();
  }
  
  @Test
  public void serviceShouldUpdatePostIfFound() throws Exception
  {
    //given
    given(postRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
    postService.updatePost(1, first);
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository)
    .should(times(1))
    .save(first);
    then(postRepository).shouldHaveNoMoreInteractions();
  }
  
  @Test
  public void serviceShouldThrowExceptionWhenTryToUpdatePostNotFound()
  {
    //given
    given(postRepository.findById(anyInt()))
    .willReturn(Optional.empty());
    
    //???
    assertThrows(NotFoundException.class, () ->
      postService.updatePost(1, first));
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  public void serviceShouldAddNewPost()
  {
    //given
    //when
    Post result = postService.addPost(first);
    
    //then
    ArgumentCaptor<Post> postCaptor = ArgumentCaptor.forClass(Post.class);
    then(postRepository)
    .should(times(1))
    .save(postCaptor.capture());
    assertThat(result.getMessage(), Matchers.is(first.getMessage()));
    
    Post postArgument = postCaptor.getValue();
    assertThat(postArgument.getMessage(), Matchers.is(first.getMessage()));
    assertNull(postArgument.getId());
  }
  
  @Test void serviceShouldReturnListOfPosts()
  {
    //given
    Page<Post> posts = Page.empty();
    given(postRepository.findAll(any(Pageable.class)))
    .willReturn(posts);
    
    //when
    Page<Post> result =  postService.findAll(0);
    
    //then
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    
    then(postRepository)
    .should(times(1))
    .findAll(pageableCaptor.capture());
    then(postRepository).shouldHaveNoMoreInteractions();
    assertThat(result, Matchers.is(posts));
    
    Pageable pageableArgument = pageableCaptor.getValue();
    assertEquals(pageableArgument.getPageNumber(), 0);
  }
  
  @Test void serviceShouldReturnListOfUserPosts()
  {
    //given
    Page<Post> posts = Page.empty();
    given(postRepository.findAllByUserId(anyInt(), any(Pageable.class)))
    .willReturn(posts);
    
    //when
    Page<Post> result =  postService.findAllByUserId(1, 0);
    
    //then
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    ArgumentCaptor<Integer> integerCaptor = ArgumentCaptor.forClass(Integer.class);
    
    then(postRepository)
    .should(times(1))
    .findAllByUserId(integerCaptor.capture(), pageableCaptor.capture());
    then(postRepository).shouldHaveNoMoreInteractions();
    assertThat(result, Matchers.is(posts));
    
    Pageable pageableArgument = pageableCaptor.getValue();
    assertEquals(pageableArgument.getPageNumber(), 0);
  }
  
  @Test
  public void serviceShouldDeletePostIfFound2() throws Exception
  {
    //given
    given(postRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
    postService.deletePost(1, 1);
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository)
    .should(times(1))
    .delete(first);
    then(postRepository).shouldHaveNoMoreInteractions();
  }
  
  @Test
  public void serviceShouldntDeletePostIfFoundUnauthorized() throws Exception
  {
    //given
    given(postRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
     assertThrows(UserUnauthorized.class, () ->
      postService.deletePost(1, 2));
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository)
    .should(never())
    .delete(first);
    then(postRepository).shouldHaveNoMoreInteractions();
  }
  
  
  @Test
  public void serviceShouldUpdatePostIfFound2() throws Exception
  {
    //given
    given(postRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
    postService.updatePost(1, 1, first);
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository)
    .should(times(1))
    .save(first);
    then(postRepository).shouldHaveNoMoreInteractions();
  }
  
  @Test
  public void serviceShouldThrowExceptionWhenTryToUpdatePostNotFound2()
  {
    //given
    given(postRepository.findById(anyInt()))
    .willReturn(Optional.empty());
    
    //???
    assertThrows(NotFoundException.class, () ->
      postService.updatePost(1, 1, first));
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository).shouldHaveNoMoreInteractions();
  }
  
  
  @Test
  public void serviceShouldntUpdatePostIfFoundUnauthorized() throws Exception
  {
    //given
    given(postRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
     assertThrows(UserUnauthorized.class, () ->
      postService.updatePost(1, 2, first));
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository)
    .should(never())
    .save(first);
    then(postRepository).shouldHaveNoMoreInteractions();
  }
}
