package org.example.best.user_profile.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "notifications")
public class UserNotificationSetting {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "notify_new_events", nullable = false)
    private Boolean notifyNewEvents = true;

    @Column(name = "notify_upcoming", nullable = false)
    private Boolean notifyUpcoming = true;

    @Column(name = "notify_before_hours")
    private Integer notifyBeforeHours = 24;

}
