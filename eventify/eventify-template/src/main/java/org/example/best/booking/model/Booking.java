package org.example.best.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.best.event.model.Event;
import org.example.best.user_profile.model.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ticket_count")
    private Integer ticketCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expire_time")
    private LocalDateTime expiryTime;

    @Column(name = "timezone")
    private String timezone;

    @Column(name = "confirmed")
    private Boolean confirmed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;
}
