package com.blankcil.api.blankcilapi.entity;

import com.blankcil.api.blankcilapi.user.Role;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class UserEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String fullname;
  private String email;
  private String password;
  private LocalDate birthday;
  private String address;
  private String phone;
  private String code;
  private LocalDateTime createDay;


  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToMany(mappedBy = "userEntity")
  private List<TokenEntity> tokens;

  @OneToMany(mappedBy = "user_comment")
  private List<CommentEntity> comments;

  @OneToMany(mappedBy = "user_podcast")
  private List<PodcastEntity> podcasts;

  @OneToMany(mappedBy = "user_comment_like")
  private List<CommentLikeEntity> comment_likes;

  @OneToMany(mappedBy = "user_podcast_like")
  public List<PodcastLikeEntity> podcast_likes;

  @OneToMany(mappedBy = "user_reply")
  public List<ReplyEntity> user_replies;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return role.getAuthorities();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
