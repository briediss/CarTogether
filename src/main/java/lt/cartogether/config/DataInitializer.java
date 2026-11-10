package lt.cartogether.config;

import lt.cartogether.model.Trip;
import lt.cartogether.repository.TripRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TripRepository tripRepository;

    public DataInitializer(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public void run(String... args) {
        if (tripRepository.count() > 0) {
            return;
        }

        LocalDateTime departureTime = LocalDate.now()
                .plusDays(1)
                .atTime(15, 0);

        Trip trip1 = new Trip();
        trip1.setTenantId(1L);
        trip1.setDriverName("Titas");
        trip1.setDepartureCity("Vilnius");
        trip1.setArrivalCity("Kaunas");
        trip1.setDepartureTime(departureTime);
        trip1.setAvailableSeats(3);
        trip1.setTotalSeats(3);
        trip1.setDistanceKm(100);
        trip1.setFuelConsumption(6);
        trip1.setFuelPrice(2.3);
        trip1.setDriverRating(4.8);
        trip1.setDetourMinutes(5);

        Trip trip2 = new Trip();
        trip2.setTenantId(1L);
        trip2.setDriverName("Lukas");
        trip2.setDepartureCity("Vilnius");
        trip2.setArrivalCity("Kaunas");
        trip2.setDepartureTime(departureTime.plusMinutes(20));
        trip2.setAvailableSeats(2);
        trip2.setTotalSeats(2);
        trip2.setDistanceKm(100);
        trip2.setFuelConsumption(7);
        trip2.setFuelPrice(2.3);
        trip2.setDriverRating(4.5);
        trip2.setDetourMinutes(10);

        Trip trip3 = new Trip();
        trip3.setTenantId(2L);
        trip3.setDriverName("Edvinas");
        trip3.setDepartureCity("Kaunas");
        trip3.setArrivalCity("Vilnius");
        trip3.setDepartureTime(departureTime);
        trip3.setAvailableSeats(3);
        trip3.setTotalSeats(3);
        trip3.setDistanceKm(100);
        trip3.setFuelConsumption(6.5);
        trip3.setFuelPrice(2.3);
        trip3.setDriverRating(4.9);
        trip3.setDetourMinutes(20);

        Trip trip4 = new Trip();
        trip4.setTenantId(2L);
        trip4.setDriverName("Klemensas");
        trip4.setDepartureCity("Vilnius");
        trip4.setArrivalCity("Klaipėda");
        trip4.setDepartureTime(departureTime.plusHours(2));
        trip4.setAvailableSeats(1);
        trip4.setTotalSeats(1);
        trip4.setDistanceKm(300);
        trip4.setFuelConsumption(7);
        trip4.setFuelPrice(2.3);
        trip4.setDriverRating(4.2);
        trip4.setDetourMinutes(0);

        tripRepository.save(trip1);
        tripRepository.save(trip2);
        tripRepository.save(trip3);
        tripRepository.save(trip4);
    }
}
