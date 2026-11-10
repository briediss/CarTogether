package lt.cartogether;

import jakarta.transaction.Transactional;
import lt.cartogether.exception.TripNotFoundException;
import lt.cartogether.model.Trip;
import lt.cartogether.repository.TripRepository;
import lt.cartogether.service.BookingService;
import lt.cartogether.service.TenantService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:cartogether_test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})

public class TenantIsolationIntegrationTest {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private TenantService tenantService;

    @Test
    @Transactional
    void tenantCannotBookAnotherTenantsTrip() {

        Trip trip = new Trip();
        trip.setTenantId(1L);
        trip.setDriverName("Test Driver");
        trip.setDepartureCity("Vilnius");
        trip.setArrivalCity("Kaunas");
        trip.setDepartureTime(LocalDateTime.of(2026, 11, 11, 15, 0));
        trip.setAvailableSeats(3);
        trip.setTotalSeats(3);
        trip.setDistanceKm(100);
        trip.setFuelConsumption(6);
        trip.setFuelPrice(2.3);
        trip.setDriverRating(4.8);
        trip.setDetourMinutes(5);

        trip = tripRepository.saveAndFlush(trip);

        Long tripId = trip.getId();
        Long otherTenantId = tenantService.getTenantId("demo-kaunas-key");

        assertThrows(TripNotFoundException.class, () -> bookingService.bookTrip(otherTenantId, tripId));
        Trip savedTrip = tripRepository.findById(tripId).orElseThrow();
        assertEquals(3, savedTrip.getAvailableSeats());
    }
}
