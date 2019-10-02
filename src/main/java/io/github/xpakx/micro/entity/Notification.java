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
import javax.validation.constraints.Size;

@Entity
@Data
public class Notification
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  
  private boolean visited;

  @ManyToOne(fetch=FetchType.LAZY, optional=false)
  @JoinColumn(name="user_id", nullable=false)
  @OnDelete(action=OnDeleteAction.CASCADE)
  private User user;  
  
  @ManyToOne(fetch=FetchType.LAZY, optional=false)
  @JoinColumn(name="caller_id", nullable=false)
  @OnDelete(action=OnDeleteAction.CASCADE)
  private User caller; 
  
  @ManyToOne(fetch=FetchType.LAZY, optional=false)
  @JoinColumn(name="post_id", nullable=false)
  @OnDelete(action=OnDeleteAction.CASCADE)
  private Post post;  
}
