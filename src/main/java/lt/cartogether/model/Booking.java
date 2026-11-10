package lt.cartogether.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long tenantId;
    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;
    private LocalDateTime bookingTime;
    private boolean cancelled = false;

    public Booking() {
    }
}
