package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.ResourcesStatus;
import com.devteria.identity_service.enums.ResourcesType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Resources {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "resourcesID", nullable = false)
  String resourcesID;

  @Size(max = 255)
  @Column(name = "resources_name")
  String resourcesName;

  @Size(max = 255)
  @Column(name = "img")
  String img;

  @Lob
  @Column(name = "description", columnDefinition = "TEXT")
  String description;

  @Lob
  @Column(name = "content", columnDefinition = "TEXT")
  String content;

  @Column(name = "reading_time")
  Integer readingTime;

  @Column(name = "resources_type")
  @Enumerated(EnumType.STRING)
  ResourcesType resourcesType;

  @Column(name = "resources_status")
  @Enumerated(EnumType.STRING)
  ResourcesStatus resourcesStatus;

  @Column(name = "created_at")
  Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @OnDelete(action = OnDeleteAction.SET_NULL)
  @JoinColumn(name = "userID", nullable = true)
  User user;
}
