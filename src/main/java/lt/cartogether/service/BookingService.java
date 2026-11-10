package lt.cartogether.service;

import jakarta.transaction.Transactional;
import lt.cartogether.exception.TripNotFoundException;
import lt.cartogether.model.Booking;
import lt.cartogether.model.Trip;
import lt.cartogether.repository.BookingRepository;
import lt.cartogether.repository.TripRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;

    public BookingService(TripRepository tripRepository, BookingRepository bookingRepository) {
        this.tripRepository = tripRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public Booking bookTrip(Long tenantId, Long tripId) {

        if(tenantId == null || tripId == null) {
            throw new IllegalArgumentException("Missing booking parameters");
        }

        Trip trip = tripRepository.findByIdAndTenantId(tripId, tenantId).orElseThrow(() -> new TripNotFoundException("Trip not found"));

        int updatedRows = tripRepository.reserveSeat(tripId, tenantId);

        if(updatedRows == 0) {
            throw new IllegalStateException("No available seats");
        }

        Booking booking = new Booking();
        booking.setTenantId(tenantId);
        booking.setTrip(trip);
        booking.setBookingTime(LocalDateTime.now());
        booking.setCancelled(false);

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookings(Long tenantId) {
        return bookingRepository.findByTenantId(tenantId);
    }

    @Transactional
    public Booking cancelBooking(Long tenantId, Long bookingId){

        Booking booking = bookingRepository.findByIdAndTenantId(bookingId, tenantId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if(booking.isCancelled()) {
            throw new IllegalStateException("Booking already cancelled");
        }

        Long tripId = booking.getTrip().getId();

        int updatedRows = bookingRepository.cancelBooking(bookingId, tenantId);
        if(updatedRows == 0) {
            throw new IllegalStateException("Booking already cancelled");
        }

        int returnedSeats = tripRepository.returnSeat(tripId, tenantId);
        if(returnedSeats == 0) {
            throw new IllegalStateException("Unable to return seats");
        }

        return bookingRepository.findByIdAndTenantId(bookingId, tenantId).orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }
}
