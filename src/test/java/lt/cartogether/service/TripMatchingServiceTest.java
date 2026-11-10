package lt.cartogether.service;

import lt.cartogether.model.Trip;
import lt.cartogether.repository.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TripMatchingServiceTest {

    private TripRepository tripRepository;
    private TripMatchingService service;
    private LocalDateTime departureTime;

    @BeforeEach
    public void setUp() {
        tripRepository = mock(TripRepository.class);
        service = new TripMatchingService(tripRepository);
        departureTime = LocalDateTime.of(2026, 11, 11, 15, 0);
    }

    private Trip createTrip(Long tenantId) {
        Trip trip = new Trip();
        trip.setTenantId(tenantId);
        trip.setDriverName("Titas");
        trip.setDepartureCity("Vilnius");
        trip.setArrivalCity("Kaunas");
        trip.setDepartureTime(departureTime);
        trip.setAvailableSeats(3);
        trip.setTotalSeats(3);
        trip.setDistanceKm(100);
        trip.setFuelConsumption(6);
        trip.setFuelPrice(2.3);
        trip.setDriverRating(4.8);
        trip.setDetourMinutes(5);
        return trip;
    }

    @Test
    void searchTripsSuccess() {

        Trip trip = createTrip(1L);
        when(tripRepository.findByTenantId(1L)).thenReturn(List.of(trip));
        List<Trip> results = service.searchTrips(1L, "Vilnius", "Kaunas", departureTime);
        assertEquals(1, results.size());
        assertEquals("Titas", results.get(0).getDriverName());
    }

    @Test
    void searchTripsTimeBoundary() {

        Trip trip = createTrip(1L);
        trip.setDepartureTime(departureTime.plusMinutes(30));
        when(tripRepository.findByTenantId(1L)).thenReturn(List.of(trip));
        List<Trip> results = service.searchTrips(1L, "Vilnius", "Kaunas", departureTime);
        assertEquals(1, results.size());
    }

    @Test
    void searchTripsInvalidCities() {

        assertThrows(IllegalArgumentException.class, () -> service.searchTrips(1L, "Vilnius", "Vilnius", departureTime));
    }

    @Test
    void calculatePriceTest() {

        Trip trip = createTrip(1L);
        double price = service.calculatePrice(trip);
        assertEquals(3.45, price, 0.001);
    }

    @Test
    void tenantIsolationTest() {

        Trip trip = createTrip(1L);
        when(tripRepository.findByTenantId(1L)).thenReturn(List.of(trip));
        when(tripRepository.findByTenantId(2L)).thenReturn(List.of());
        List<Trip> results = service.searchTrips(2L, "Vilnius", "Kaunas", departureTime);
        assertTrue(results.isEmpty());
        verify(tripRepository, never()).findByTenantId(1L);
    }

    @Test
    void searchTripsNoAvailableSeats() {

        Trip trip = createTrip(1L);
        trip.setAvailableSeats(0);
        when(tripRepository.findByTenantId(1L)).thenReturn(List.of(trip));
        List<Trip> results = service.searchTrips(1L, "Vilnius", "Kaunas", departureTime);
        assertTrue(results.isEmpty());
    }

    @Test
    void priceDoesNotChangeAfterBooking() {

        Trip trip = createTrip(1L);
        double priceBefore = service.calculatePrice(trip);
        trip.setAvailableSeats(2);
        double priceAfter = service.calculatePrice(trip);
        assertEquals(priceBefore, priceAfter);
        assertEquals(3.45, priceAfter, 0.001);
    }
}
