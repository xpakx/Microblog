package io.github.xpakx.micro.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.xpakx.micro.repository.CommentRepository;
import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.entity.Comment;
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
import org.hamcrest.Matchers;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.ArgumentCaptor;


import java.util.List;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
public class CommentServiceTest 
{

  @Mock
  private CommentRepository commentRepository;
  
  @InjectMocks
  private CommentService commentService;
  
  Comment first;
  Comment second;
  Comment third;
  User user;
  Post post;

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
    post = new Post();
    post.setId(1);
    post.setUser(user);
    first = new Comment();
    first.setId(1); 
    first.setMessage("msg1");
    first.setUser(user);
    first.setPost(post);
    second = new Comment();
    second.setId(2); 
    second.setMessage("msg2");
    second.setUser(user);
    second.setPost(post);
    third = new Comment();
    third.setId(3); 
    third.setMessage("msg3");
    third.setUser(user);
    third.setPost(post);
  }

  @Test
  public void serviceShouldReturnCommentIfPostFound() throws Exception 
  {
    //given
    given(commentRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
    Comment result = commentService.findById(1);
    
    //then
    then(commentRepository)
    .should(times(1))
    .findById(1);
    then(commentRepository).shouldHaveNoMoreInteractions();
    assertNotNull(result);
    assertThat(result, Matchers.is(first));
  }
  
  @Test
  public void serviceShouldReturnNullIfCommentNotFound() throws Exception
  {
    //given
    given(commentRepository.findById(anyInt()))
    .willReturn(Optional.empty());
    
    //when
    Comment result = commentService.findById(1);
    
    //then
    then(commentRepository)
    .should(times(1))
    .findById(1);
    then(commentRepository).shouldHaveNoMoreInteractions();
    assertNull(result);
  }
  
  @Test
  public void serviceShouldDeleteCommentIfFound() throws Exception
  {
    //given
    given(commentRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
    Integer result = commentService.deleteComment(1);
    
    //then
    then(commentRepository)
    .should(times(1))
    .findById(1);
    then(commentRepository)
    .should(times(1))
    .delete(first);
    then(commentRepository).shouldHaveNoMoreInteractions();
    assertThat(result, Matchers.is(1));
  }
  
  @Test
  public void serviceShouldThrowExceptionWhenTryToDeleteCommentNotFound()
  {
    //given
    given(commentRepository.findById(anyInt()))
    .willReturn(Optional.empty());
    
    //???
    assertThrows(UserNotFound.class, () ->
      commentService.deleteComment(1));
    
    //then
    then(commentRepository)
    .should(times(1))
    .findById(1);
    then(commentRepository).shouldHaveNoMoreInteractions();
  }
  
  @Test
  public void serviceShouldUpdateCommentIfFound() throws Exception
  {
    //given
    given(commentRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
    Integer result = commentService.updateComment(1, first);
    
    //then
    then(commentRepository)
    .should(times(1))
    .findById(1);
    then(commentRepository)
    .should(times(1))
    .save(first);
    then(commentRepository).shouldHaveNoMoreInteractions();
    assertThat(result, Matchers.is(1));
  }
  
  @Test
  public void serviceShouldThrowExceptionWhenTryToUpdateCommentNotFound()
  {
    //given
    given(commentRepository.findById(anyInt()))
    .willReturn(Optional.empty());
    
    //???
    assertThrows(UserNotFound.class, () ->
      commentService.updateComment(1, first));
    
    //then
    then(commentRepository)
    .should(times(1))
    .findById(1);
    then(commentRepository).shouldHaveNoMoreInteractions();
  }

  @Test
  public void serviceShouldAddNewComment()
  {
    //given
    //when
    Comment result = commentService.addComment(first);
    
    //then
    ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
    then(commentRepository)
    .should(times(1))
    .save(commentCaptor.capture());
    assertThat(result.getMessage(), Matchers.is(first.getMessage()));
    
    Comment commentArgument = commentCaptor.getValue();
    assertThat(commentArgument.getMessage(), Matchers.is(first.getMessage()));
    assertNull(commentArgument.getId());
  }
  
  @Test
  public void serviceShouldDeleteCommentIfFound2() throws Exception
  {
    //given
    given(commentRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
    Integer result = commentService.deleteComment(1, 1);
    
    //then
    then(commentRepository)
    .should(times(1))
    .findById(1);
    then(commentRepository)
    .should(times(1))
    .delete(first);
    then(commentRepository).shouldHaveNoMoreInteractions();
    assertThat(result, Matchers.is(1));
  }
  
  @Test
  public void serviceShouldntDeleteCommentIfFoundUnauthorized() throws Exception
  {
    //given
    given(commentRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
     assertThrows(UserUnauthorized.class, () ->
      commentService.deleteComment(1, 2));
    
    //then
    then(commentRepository)
    .should(times(1))
    .findById(1);
    then(commentRepository)
    .should(never())
    .delete(first);
    then(commentRepository).shouldHaveNoMoreInteractions();
  }
  
  
  @Test
  public void serviceShouldUpdateCommentIfFound2() throws Exception
  {
    //given
    given(commentRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
    Integer result = commentService.updateComment(1, 1, first);
    
    //then
    then(commentRepository)
    .should(times(1))
    .findById(1);
    then(commentRepository)
    .should(times(1))
    .save(first);
    then(commentRepository).shouldHaveNoMoreInteractions();
    assertThat(result, Matchers.is(1));
  }
  
  @Test
  public void serviceShouldThrowExceptionWhenTryToUpdateCommentNotFound2()
  {
    //given
    given(commentRepository.findById(anyInt()))
    .willReturn(Optional.empty());
    
    //???
    assertThrows(UserNotFound.class, () ->
      commentService.updateComment(1, 1, first));
    
    //then
    then(commentRepository)
    .should(times(1))
    .findById(1);
    then(commentRepository).shouldHaveNoMoreInteractions();
  }
  
  
  @Test
  public void serviceShouldntUpdateCommentIfFoundUnauthorized() throws Exception
  {
    //given
    given(commentRepository.findById(anyInt()))
    .willReturn(Optional.of(first));
    
    //when
     assertThrows(UserUnauthorized.class, () ->
      commentService.updateComment(1, 2, first));
    
    //then
    then(commentRepository)
    .should(times(1))
    .findById(1);
    then(commentRepository)
    .should(never())
    .save(first);
    then(commentRepository).shouldHaveNoMoreInteractions();
  }
  
  @Test void serviceShouldReturnListOfUserComments()
  {
    //given
    Page<Comment> comments = Page.empty();
    given(commentRepository.findAllByUserId(anyInt(), any(Pageable.class)))
    .willReturn(comments);
    
    //when
    Page<Comment> result =  commentService.findAllByUserId(1, 0);
    
    //then
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    ArgumentCaptor<Integer> integerCaptor = ArgumentCaptor.forClass(Integer.class);
    
    then(commentRepository)
    .should(times(1))
    .findAllByUserId(integerCaptor.capture(), pageableCaptor.capture());
    then(commentRepository).shouldHaveNoMoreInteractions();
    assertThat(result, Matchers.is(comments));
    
    Pageable pageableArgument = pageableCaptor.getValue();
    assertEquals(pageableArgument.getPageNumber(), 0);
  }
  
  @Test void serviceShouldReturnListOfPostComments()
  {
    //given
    Page<Comment> comments = Page.empty();
    given(commentRepository.findAllByPostId(anyInt(), any(Pageable.class)))
    .willReturn(comments);
    
    //when
    Page<Comment> result =  commentService.findAllByPostId(1, 0);
    
    //then
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    ArgumentCaptor<Integer> integerCaptor = ArgumentCaptor.forClass(Integer.class);
    
    then(commentRepository)
    .should(times(1))
    .findAllByPostId(integerCaptor.capture(), pageableCaptor.capture());
    then(commentRepository).shouldHaveNoMoreInteractions();
    assertThat(result, Matchers.is(comments));
    
    Pageable pageableArgument = pageableCaptor.getValue();
    assertEquals(pageableArgument.getPageNumber(), 0);
  }
  
  
  @Test void serviceShouldReturnListOfPostComments2()
  {
    //given
    Page<Comment> comments = Page.empty();
    given(commentRepository.findAllByPostId(anyInt(), any(Pageable.class)))
    .willReturn(comments);
    
    //when
    Page<Comment> result =  commentService.findTwoByPostId(1);
    
    //then
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    ArgumentCaptor<Integer> integerCaptor = ArgumentCaptor.forClass(Integer.class);
    
    then(commentRepository)
    .should(times(1))
    .findAllByPostId(integerCaptor.capture(), pageableCaptor.capture());
    then(commentRepository).shouldHaveNoMoreInteractions();
    assertThat(result, Matchers.is(comments));
    
    Pageable pageableArgument = pageableCaptor.getValue();
    assertEquals(pageableArgument.getPageNumber(), 0);
    assertEquals(pageableArgument.getPageSize(), 2);
  }
}
