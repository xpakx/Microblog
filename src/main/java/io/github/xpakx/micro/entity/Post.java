package io.github.xpakx.micro.entity;

import javax.persistence.Entity;
import lombok.Data;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;

import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import org.hibernate.validator.constraints.NotEmpty;
import javax.persistence.Transient;
import java.util.List;

@Entity
@Data
public class Post
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  
  @Column(nullable = false)
  @NotEmpty
  private String message;


  @ManyToOne(fetch=FetchType.LAZY, optional=false)
  @JoinColumn(name="user_id", nullable=false)
  @OnDelete(action=OnDeleteAction.CASCADE)
  private User user;  
  
  @Transient
  private List<Comment> comments;
}
