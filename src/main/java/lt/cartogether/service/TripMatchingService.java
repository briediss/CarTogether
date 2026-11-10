package lt.cartogether.service;

import lt.cartogether.model.Trip;
import lt.cartogether.repository.TripRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class TripMatchingService {

    private final TripRepository tripRepository;

    public TripMatchingService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<Trip> searchTrips(Long tenantId, String departureCity, String arrivalCity, LocalDateTime departureTime){

        if (tenantId == null || departureCity == null || arrivalCity == null || departureTime == null){
            throw new IllegalArgumentException("Missing search parameters");
        }

        if (departureCity.isBlank() || arrivalCity.isBlank()){
            throw new IllegalArgumentException("Cities cannot be empty");
        }

        if (departureCity.equalsIgnoreCase(arrivalCity)){
            throw new IllegalArgumentException("Departure and arrival cities must be different");
        }

        return tripRepository.findByTenantId(tenantId).stream()
                .filter(trip -> trip.getDepartureCity().equalsIgnoreCase(departureCity))
                .filter(trip -> trip.getArrivalCity().equalsIgnoreCase(arrivalCity))
                .filter(trip -> trip.getAvailableSeats() > 0)
                .filter(trip -> trip.getDriverRating() >= 3.0)
                .filter(trip -> isTimeSuitable(trip.getDepartureTime(), departureTime))
                .sorted(Comparator.comparing(trip -> Math.abs(Duration.between(departureTime, trip.getDepartureTime()).toMinutes())))
                .toList();
    }

    private boolean isTimeSuitable(LocalDateTime tripTime, LocalDateTime requestedTime){

        long difference = Math.abs(Duration.between(requestedTime, tripTime).toMinutes());
        return difference <= 30;
    }

    public double calculatePrice(Trip trip){
        double fuelCost = trip.getDistanceKm()/100.0  * trip.getFuelConsumption() * trip.getFuelPrice();
        return Math.round(fuelCost / (trip.getTotalSeats() + 1) * 100.0) / 100.0;
    }
}
