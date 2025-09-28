package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.AppointmentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "Availability")
public class Availability {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "AvailabilityID", nullable = false)
  private String availabilityID;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "coachID", nullable = true)
  @JsonIgnore
  private User coach;

  String reason;

  @Column(name = "availability_datetime")
  private Instant availabilityDatetime;

  @Column(name = "created_at")
  private Instant createdAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private AppointmentStatus status;

  @PrePersist
  protected void onCreate() {
    this.createdAt = Instant.now();
  }
}
