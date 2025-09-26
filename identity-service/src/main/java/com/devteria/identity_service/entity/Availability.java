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
@Table(name = "Availability")
public class Availability {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "AvailabilityID", nullable = false)
    private String availabilityID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coachID", nullable = true)
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
