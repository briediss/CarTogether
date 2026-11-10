package lt.cartogether.service;

import lt.cartogether.exception.TripNotFoundException;
import lt.cartogether.model.Trip;
import lt.cartogether.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    private TripRepository tripRepository;
    private BookingService service;

    @BeforeEach
    void setUp() {
        tripRepository = mock(TripRepository.class);
        service = new BookingService(tripRepository);
    }

    private Trip createTrip(int availableSeats) {

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setTenantId(1L);
        trip.setDriverName("Titas");
        trip.setAvailableSeats(availableSeats);
        trip.setTotalSeats(3);
        return trip;
    }

    @Test
    void bookingSuccess() {

        Trip tripBefore = createTrip(3);
        Trip tripAfter = createTrip(2);
        when(tripRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(tripBefore), Optional.of(tripAfter));
        when(tripRepository.reserveSeat(1L, 1L)).thenReturn(1);
        Trip result = service.bookTrip(1L, 1L);
        assertEquals(2, result.getAvailableSeats());
        assertEquals(3, result.getTotalSeats());
        verify(tripRepository).reserveSeat(1L, 1L);
    }

    @Test
    void bookingNoAvailableSeats() {

        Trip trip = createTrip(0);
        when(tripRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(trip));
        when(tripRepository.reserveSeat(1L, 1L)).thenReturn(0);
        assertThrows(IllegalStateException.class, () -> service.bookTrip(1L, 1L));
    }

    @Test
    void bookingOtherTenant() {

        when(tripRepository.findByIdAndTenantId(1L, 2L)).thenReturn(Optional.empty());
        assertThrows(TripNotFoundException.class, () -> service.bookTrip(2L, 1L));
        verify(tripRepository, never()).reserveSeat(anyLong(), anyLong());
    }
}
