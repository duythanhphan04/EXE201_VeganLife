package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.Role;
import com.devteria.identity_service.enums.UserPlan;
import com.devteria.identity_service.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "User")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Size(max = 255)
  @Column(name = "userID", nullable = false)
  String userID;

  @Size(max = 255)
  @NotNull
  @Column(name = "username", nullable = false)
  String username;

  @Size(max = 255)
  @Column(name = "password", nullable = true)
  String password;

  @Size(max = 255)
  @Column(name = "email")
  String email;

  @Size(max = 255)
  @Column(name = "fullName")
  String fullName;

  @Size(max = 255)
  @Column(name = "img")
  String img;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.SET_NULL)
  @JoinColumn(name = "personal_goal")
  PersonalGoal personalGoal;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.SET_NULL)
  @JoinColumn(name = "coach")
  User coach;

  @Column(name = "plan", length = 255)
  @Enumerated(EnumType.STRING)
  UserPlan plan;

  @Column(name = "created_at")
  Instant createdAt;

  @Column(name = "role", length = 255)
  @Enumerated(EnumType.STRING)
  Role role;

  @Column(name = "status", length = 255)
  @Enumerated(EnumType.STRING)
  UserStatus status;

  @OneToMany(mappedBy = "coach")
  private List<Availability> availabilities = new ArrayList<>();
}
