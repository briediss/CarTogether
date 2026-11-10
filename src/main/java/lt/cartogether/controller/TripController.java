package lt.cartogether.controller;

import lt.cartogether.dto.TripResponse;
import lt.cartogether.model.Trip;
import lt.cartogether.service.TenantService;
import lt.cartogether.service.TripMatchingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripMatchingService tripMatchingService;
    private final TenantService tenantService;

    public TripController(TripMatchingService tripMatchingService, TenantService tenantService) {
        this.tripMatchingService = tripMatchingService;
        this.tenantService = tenantService;
    }

    @GetMapping("/search")
    public List<TripResponse> searchTrips(
        @RequestHeader("X-API-Key") String apiKey,
        @RequestParam("departureCity") String departureCity,
        @RequestParam("arrivalCity") String arrivalCity,
        @RequestParam("departureTime")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime) {

        Long tenantId = tenantService.getTenantId(apiKey);

        List<Trip> trips = tripMatchingService.searchTrips(tenantId, departureCity, arrivalCity, departureTime);
        return trips.stream().map(trip -> new TripResponse(
            trip.getId(),
            trip.getDriverName(),
            trip.getDepartureCity(),
            trip.getArrivalCity(),
            trip.getDepartureTime(),
            trip.getAvailableSeats(),
            trip.getDriverRating(),
            tripMatchingService.calculatePrice(trip),
            trip.getDetourMinutes()
        )).toList();
    }
}

