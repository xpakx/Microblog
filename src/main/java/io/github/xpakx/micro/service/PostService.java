package io.github.xpakx.micro.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.repository.PostRepository;
import io.github.xpakx.micro.error.UserNotFound;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class PostService
{
  private PostRepository postRepository;
  
  @Autowired
  public PostService(PostRepository postRepository)
  {
    this.postRepository = postRepository;
  }
  
  public Post findById(Integer i)
  {
    return postRepository.findById(i).orElse(null);
  }
  
  public void deletePost(Integer i) throws UserNotFound
  {
    Optional<Post> post =  postRepository.findById(i);
    postRepository.delete(post.orElseThrow(() -> new UserNotFound("User not found!")));
  }
  
  public void updatePost(Integer i, Post post)
  {
    Optional<Post> foundPost = postRepository.findById(i);
    Post fPost = foundPost.orElseThrow(() -> new UserNotFound("User not found!"));
    fPost.setMessage(post.getMessage());
    postRepository.save(fPost);
  }
  
  public Post addPost(Post post)
  {
    post.setId(null);
    postRepository.save(post);

    return post;
  }
    
  public Page<Post> findAll(Integer page)
  {
    Pageable nthPageWith20Elements = PageRequest.of(page, 20, Sort.by("id").descending());
    Page<Post> allPosts = postRepository.findAll(nthPageWith20Elements);
    
    return allPosts;
  }
  
  public Page<Post> findAllByUserId(Integer userId, Integer page)
  {
    Pageable nthPageWith20Elements = PageRequest.of(page, 20, Sort.by("id").descending());
    Page<Post> allPosts = postRepository.findAllByUserId(userId, nthPageWith20Elements);
    
    return allPosts;   
  }



}
