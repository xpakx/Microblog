package io.github.xpakx.micro.entity;

import javax.persistence.Entity;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Email;
import javax.validation.constraints.Size;
import javax.persistence.ManyToMany;
import javax.persistence.CascadeType;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;

import java.util.List;

@Entity
@Data
public class User
{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;
  
  @Column(nullable=false, unique=true)
  @NotEmpty
  private String username;
  
  @Column(nullable=false, unique=true)
  @NotEmpty
  @Email(message="Niepoprawny adres e-mail")
  private String email;
  
  @Column(nullable=false)
  @NotEmpty
  @Size(min=6)
  private String password;
  
  @ManyToMany(cascade=CascadeType.MERGE)
  @JoinTable(name="user_role", 
             joinColumns={@JoinColumn(name="user_id",  referencedColumnName="id")},
             inverseJoinColumns={@JoinColumn(name="role_id",  referencedColumnName="id")})
  private List<UserRole> roles;
}
