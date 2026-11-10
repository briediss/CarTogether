package lt.cartogether.service;

import lt.cartogether.exception.TripNotFoundException;
import lt.cartogether.model.Booking;
import lt.cartogether.model.Trip;
import lt.cartogether.repository.BookingRepository;
import lt.cartogether.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    private TripRepository tripRepository;
    private BookingRepository bookingRepository;
    private BookingService service;

    @BeforeEach
    void setUp() {
        tripRepository = mock(TripRepository.class);
        bookingRepository = mock(BookingRepository.class);
        service = new BookingService(tripRepository, bookingRepository);
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

        Trip trip = createTrip(3);

        when(tripRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(trip));
        when(tripRepository.reserveSeat(1L, 1L)).thenReturn(1);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = service.bookTrip(1L, 1L);

        assertEquals(1L, booking.getTenantId());
        assertEquals(trip, booking.getTrip());
        assertFalse(booking.isCancelled());
        assertNotNull(booking.getBookingTime());

        verify(tripRepository).reserveSeat(1L, 1L);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void bookingNoAvailableSeats() {

        Trip trip = createTrip(0);

        when(tripRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(trip));
        when(tripRepository.reserveSeat(1L, 1L)).thenReturn(0);

        assertThrows(IllegalStateException.class, () -> service.bookTrip(1L, 1L));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void bookingOtherTenant() {

        when(tripRepository.findByIdAndTenantId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(TripNotFoundException.class, () -> service.bookTrip(2L, 1L));

        verify(tripRepository, never()).reserveSeat(anyLong(), anyLong());
    }

    @Test
    void cancelBookingSuccess() {
        Trip trip = createTrip(2);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTenantId(1L);
        booking.setTrip(trip);
        booking.setCancelled(false);

        Booking cancelledBooking = new Booking();
        cancelledBooking.setId(1L);
        cancelledBooking.setTenantId(1L);
        cancelledBooking.setTrip(trip);
        cancelledBooking.setCancelled(true);

        when(bookingRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(booking), Optional.of(cancelledBooking));
        when(bookingRepository.cancelBooking(1L, 1L)).thenReturn(1);
        when(tripRepository.returnSeat(1L, 1L)).thenReturn(1);

        Booking result = service.cancelBooking(1L, 1L);

        assertTrue(result.isCancelled());

        verify(bookingRepository).cancelBooking(1L, 1L);
        verify(tripRepository).returnSeat(1L, 1L);
    }

    @Test
    void cancelBookingTwice() {

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTenantId(1L);
        booking.setCancelled(true);

        when(bookingRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class, () -> service.cancelBooking(1L, 1L));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void cancelBookingReturnSeatFailed() {
        Trip trip = createTrip(2);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setTenantId(1L);
        booking.setTrip(trip);
        booking.setCancelled(false);

        when(bookingRepository.findByIdAndTenantId(1L, 1L)).thenReturn(Optional.of(booking));
        when(bookingRepository.cancelBooking(1L, 1L)).thenReturn(1);
        when(tripRepository.returnSeat(1L, 1L)).thenReturn(0);

        assertThrows(IllegalStateException.class, () -> service.cancelBooking(1L, 1L));
    }
}
