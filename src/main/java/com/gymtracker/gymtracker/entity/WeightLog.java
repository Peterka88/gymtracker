package com.gymtracker.gymtracker.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "weight_logs")
public class WeightLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser appUser;

    private Double weight;

    private LocalDateTime loggedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AppUser getUser() { return appUser; }
    public void setUser(AppUser appUser) { this.appUser = appUser; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public LocalDateTime getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDateTime loggedAt) { this.loggedAt = loggedAt; }
}