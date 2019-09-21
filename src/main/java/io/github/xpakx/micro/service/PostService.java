package io.github.xpakx.micro.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import io.github.xpakx.micro.entity.Post;
import io.github.xpakx.micro.repository.PostRepository;
import io.github.xpakx.micro.error.UserNotFound;

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
    return null;
  }
  
  public List<Post> findAllByUsername(String producerName, Integer page)
  {
    return null;    
  }
  
  public List<Post> findAll(Integer page)
  {
    return null;
  }


}
