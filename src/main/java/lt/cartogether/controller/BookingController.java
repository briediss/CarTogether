package lt.cartogether.controller;

import lt.cartogether.model.Trip;
import lt.cartogether.service.BookingService;
import lt.cartogether.service.TenantService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final TenantService tenantService;

    public BookingController(BookingService bookingService, TenantService tenantService) {
        this.bookingService = bookingService;
        this.tenantService = tenantService;
    }

    @PostMapping
    public Trip bookTrip(@RequestHeader("X-API-Key") String apiKey, @RequestParam Long tripId){
        Long tenantId = tenantService.getTenantId(apiKey);
        return bookingService.bookTrip(tenantId, tripId);
    }
}
