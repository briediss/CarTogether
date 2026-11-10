package lt.cartogether;

import lt.cartogether.model.Booking;
import lt.cartogether.model.Trip;
import lt.cartogether.repository.BookingRepository;
import lt.cartogether.repository.TripRepository;
import lt.cartogether.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:booking_cancellation_test;DB_CLOSE_DELAY=-1",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})

public class BookingCancellationIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TripRepository tripRepository;

    @Test
    void cancellationRollbackWhenSeatCannotBeReturned() {

        Trip trip = new Trip();
        trip.setTenantId(1L);
        trip.setDriverName("Test Driver");
        trip.setDepartureCity("Vilnius");
        trip.setArrivalCity("Kaunas");
        trip.setDepartureTime(LocalDateTime.now().plusDays(1));
        trip.setTotalSeats(2);
        trip.setAvailableSeats(2);

        trip = tripRepository.saveAndFlush(trip);

        Booking booking = new Booking();
        booking.setTenantId(1L);
        booking.setTrip(trip);
        booking.setBookingTime(LocalDateTime.now());
        booking.setCancelled(false);

        booking = bookingRepository.saveAndFlush(booking);

        Long bookingId = booking.getId();

        assertThrows(IllegalStateException.class, () -> bookingService.cancelBooking(1L, bookingId));

        Booking savedBooking = bookingRepository.findByIdAndTenantId(bookingId, 1L).orElseThrow();

        assertFalse(savedBooking.isCancelled());

        Trip savedTrip = tripRepository.findById(trip.getId()).orElseThrow();

        assertEquals(2, savedTrip.getAvailableSeats());
    }
}
