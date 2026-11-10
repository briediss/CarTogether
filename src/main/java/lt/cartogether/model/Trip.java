package lt.cartogether.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long tenantId;
    private String driverName;
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureTime;
    private int availableSeats;
    private int totalSeats;
    private double distanceKm;
    private double fuelConsumption;
    private double fuelPrice;
    private double driverRating;

    public Trip() {
    }
}
