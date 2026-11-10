package lt.cartogether.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TripResponse {

    private Long id;
    private String driverName;
    private String departureCity;
    private String arrivalCity;
    private LocalDateTime departureTime;
    private int availableSeats;
    private double driverRating;
    private double pricePerPerson;
    private Integer detourMinutes;

    public TripResponse(Long id, String driverName, String departureCity, String arrivalCity, LocalDateTime departureTime, int availableSeats, double driverRating, double pricePerPerson, Integer detourMinutes) {
        this.id = id;
        this.driverName = driverName;
        this.departureCity = departureCity;
        this.arrivalCity = arrivalCity;
        this.departureTime = departureTime;
        this.availableSeats = availableSeats;
        this.driverRating = driverRating;
        this.pricePerPerson = pricePerPerson;
        this.detourMinutes = detourMinutes;
    }
}
