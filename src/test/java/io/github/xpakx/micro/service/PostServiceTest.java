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
import io.github.xpakx.micro.error.UserNotFound;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.BDDMockito.*;
import static org.junit.Assert.*;
import org.mockito.MockitoAnnotations;
import org.hamcrest.Matchers;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;


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
    assertThrows(UserNotFound.class, () ->
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
    assertThrows(UserNotFound.class, () ->
      postService.updatePost(1, first));
    
    //then
    then(postRepository)
    .should(times(1))
    .findById(1);
    then(postRepository).shouldHaveNoMoreInteractions();
  }


}
