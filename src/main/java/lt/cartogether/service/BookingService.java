package lt.cartogether.service;

import jakarta.transaction.Transactional;
import lt.cartogether.model.Trip;
import lt.cartogether.repository.TripRepository;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final TripRepository tripRepository;

    public BookingService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Transactional
    public Trip bookTrip(Long tenantId, Long tripId) {

        if(tenantId == null || tripId == null) {
            throw new IllegalArgumentException("Missing booking parameters");
        }

        Trip trip = tripRepository.findByIdAndTenantId(tripId, tenantId).orElseThrow(() -> new IllegalArgumentException("Trip not found"));

        int updatedRows = tripRepository.reserveSeat(tripId, tenantId);

        if(updatedRows == 0) {
            throw new IllegalStateException("No available seats");
        }

        return tripRepository.findByIdAndTenantId(tripId, tenantId).orElseThrow(() -> new IllegalArgumentException("Trip not found"));
    }
}
