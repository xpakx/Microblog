package io.github.xpakx.micro.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.xpakx.micro.entity.Comment;
import io.github.xpakx.micro.repository.CommentRepository;
import io.github.xpakx.micro.error.NotFoundException;
import io.github.xpakx.micro.error.UserUnauthorized;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class CommentService
{
  private CommentRepository commentRepository;
  private NotificationService notificationService;
  
  @Autowired
  public CommentService(CommentRepository commentRepository,  NotificationService notificationService)
  {
    this.commentRepository = commentRepository;
    this.notificationService = notificationService;
  }
  
  public Comment findById(Integer i)
  {
    return commentRepository.findById(i).orElse(null);
  }
  
  public Integer deleteComment(Integer i) 
  {
    Optional<Comment> comment =  commentRepository.findById(i);
    Comment commentToDelete = comment.orElseThrow(() -> new NotFoundException("Comment not found!"));
    Integer id = commentToDelete.getPost().getId();
    commentRepository.delete(commentToDelete);
    return id;
  }
  
  public Integer deleteComment(Integer i, Integer userId) 
  {
    Optional<Comment> comment =  commentRepository.findById(i);
    Comment commentToDelete = comment.orElseThrow(() -> new NotFoundException("Comment not found!"));
    Integer id = commentToDelete.getPost().getId();
    if(commentToDelete.getUser().getId() != userId) 
    {throw new UserUnauthorized("User unauthorized!");}
    commentRepository.delete(commentToDelete);
    return id;
  }
  
  public Integer updateComment(Integer i, Comment comment)
  {
    Optional<Comment> foundComment = commentRepository.findById(i);
    Comment fComment = foundComment.orElseThrow(() -> new NotFoundException("Comment not found!"));
    fComment.setMessage(comment.getMessage());
    commentRepository.save(fComment);
    return fComment.getPost().getId();
  }
  
  public Integer updateComment(Integer i, Integer userId, Comment comment)
  {
    Optional<Comment> foundComment = commentRepository.findById(i);
    Comment fComment = foundComment.orElseThrow(() -> new NotFoundException("Comment not found!"));
    if(fComment.getUser().getId() != userId) 
    {throw new UserUnauthorized("User unauthorized!");}
    fComment.setMessage(comment.getMessage());
    commentRepository.save(fComment);
    return fComment.getPost().getId();
  }
  
  public Comment addComment(Comment comment)
  {
    comment.setId(null);
    commentRepository.save(comment);

    notificationService.addMentions(comment.getMessage(), comment.getPost(), comment.getUser());
    return comment;
  }
  
  public Page<Comment> findAllByUserId(Integer userId, Integer page)
  {
    Pageable nthPageWith20Elements = PageRequest.of(page, 20, Sort.by("id").descending());
    Page<Comment> allComments = commentRepository.findAllByUserId(userId, nthPageWith20Elements);
    
    return allComments;    
  }
  
  public Page<Comment> findAllByPostId(Integer postId, Integer page)
  {
    Pageable nthPageWith20Elements = PageRequest.of(page, 20, Sort.by("id").descending());
    Page<Comment> allComments = commentRepository.findAllByPostId(postId, nthPageWith20Elements);
    
    return allComments;     
  }
  
  public Page<Comment> findTwoByPostId(Integer postId)
  {
    Pageable first2Elements = PageRequest.of(0, 2, Sort.by("id").descending());
    Page<Comment> allComments = commentRepository.findAllByPostId(postId, first2Elements);
    
    return allComments;     
  }

}
