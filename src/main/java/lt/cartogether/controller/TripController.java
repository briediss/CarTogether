package lt.cartogether.controller;

import lt.cartogether.model.Trip;
import lt.cartogether.service.TripMatchingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripMatchingService tripMatchingService;

    public TripController(TripMatchingService tripMatchingService) {
        this.tripMatchingService = tripMatchingService;
    }

    @GetMapping("/search")
    public List<Trip> searchTrips(
        @RequestParam Long tenantId,
        @RequestParam String departureCity,
        @RequestParam String arrivalCity,
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTime) {
            return tripMatchingService.searchTrips(tenantId, departureCity, arrivalCity, departureTime);
    }
}

