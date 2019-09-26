package io.github.xpakx.micro.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.xpakx.micro.entity.Comment;
import io.github.xpakx.micro.repository.CommentRepository;
import io.github.xpakx.micro.error.UserNotFound;
import io.github.xpakx.micro.error.UserUnauthorized;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class CommentService
{
  private CommentRepository commentRepository;
  
  @Autowired
  public CommentService(CommentRepository commentRepository)
  {
    this.commentRepository = commentRepository;
  }
  
  public Comment findById(Integer i)
  {
    return commentRepository.findById(i).orElse(null);
  }
  
  public void deleteComment(Integer i) 
  {
    Optional<Comment> comment =  commentRepository.findById(i);
    Comment commentToDelete = comment.orElseThrow(() -> new UserNotFound("Comment not found!"));
    commentRepository.delete(commentToDelete);
  }
  
  public void deleteComment(Integer i, Integer userId) 
  {
    Optional<Comment> comment =  commentRepository.findById(i);
    Comment commentToDelete = comment.orElseThrow(() -> new UserNotFound("Comment not found!"));
    if(commentToDelete.getUser().getId() != userId) 
    {throw new UserUnauthorized("User unauthorized!");}
    commentRepository.delete(commentToDelete);
  }
  
  public void updateComment(Integer i, Comment comment)
  {
    Optional<Comment> foundComment = commentRepository.findById(i);
    Comment fComment = foundComment.orElseThrow(() -> new UserNotFound("Comment not found!"));
    fComment.setMessage(comment.getMessage());
    commentRepository.save(fComment);
  }
  
  public void updateComment(Integer i, Integer userId, Comment comment)
  {
    Optional<Comment> foundComment = commentRepository.findById(i);
    Comment fComment = foundComment.orElseThrow(() -> new UserNotFound("Comment not found!"));
    if(fComment.getUser().getId() != userId) 
    {throw new UserUnauthorized("User unauthorized!");}
    fComment.setMessage(comment.getMessage());
    commentRepository.save(fComment);
  }
  
  public Comment addComment(Comment comment)
  {
    comment.setId(null);
    commentRepository.save(comment);

    return comment;
  }
  
  public Page<Comment> findAllByUserId(Integer userId, Integer page)
  {
    Pageable nthPageWith20Elements = PageRequest.of(0, 2, Sort.by("id").descending());
    Page<Comment> allComments = commentRepository.findAllByUserId(userId, nthPageWith20Elements);
    
    return allComments;    
  }
  
  public Page<Comment> findAllByPostId(Integer postId, Integer page)
  {
    Pageable nthPageWith20Elements = PageRequest.of(0, 2, Sort.by("id").descending());
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
