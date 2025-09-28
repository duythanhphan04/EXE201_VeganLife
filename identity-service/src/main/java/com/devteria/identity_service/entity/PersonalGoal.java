package com.devteria.identity_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Personal_Goal")
public class PersonalGoal {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Size(max = 255)
  @Column(name = "personal_goalID", nullable = false)
  private String personalGoalid;

  @Size(max = 255)
  @NotNull
  @Column(name = "goal_name", nullable = false)
  private String goalName;
}
