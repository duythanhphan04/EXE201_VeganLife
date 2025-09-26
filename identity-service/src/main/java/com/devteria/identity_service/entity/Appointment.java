package com.devteria.identity_service.entity;

import com.devteria.identity_service.enums.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "appointmentID", nullable = false)
    String appointmentID;

    @Size(max = 255)
    @Column(name = "notes")
    String notes;

    @Size(max = 255)
    @Column(name = "link")
    String link;

    @Size(max = 255)
    @Column(name = "content")
    String content;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    AppointmentStatus status;

    @Column(name = "appointmentDateTime")
    Instant appointmentDateTime;

    @Column(name = "created_at")
    Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "userID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "coachID")
    private User coach;
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
