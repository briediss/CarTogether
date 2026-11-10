package lt.cartogether.controller;

import lt.cartogether.model.Booking;
import lt.cartogether.model.Trip;
import lt.cartogether.service.BookingService;
import lt.cartogether.service.TenantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public Booking bookTrip(@RequestHeader("X-API-Key") String apiKey, @RequestParam Long tripId){
        Long tenantId = tenantService.getTenantId(apiKey);
        return bookingService.bookTrip(tenantId, tripId);
    }

    @GetMapping
    public List<Booking> getBookings(@RequestHeader("X-API-Key") String apiKey){
        Long tenantId = tenantService.getTenantId(apiKey);
        return bookingService.getBookings(tenantId);
    }

    @PatchMapping("/{bookingId}/cancel")
    public Booking cancelBooking(@RequestHeader("X-API-Key") String apiKey, @PathVariable Long bookingId){
        Long tenantId = tenantService.getTenantId(apiKey);
        return bookingService.cancelBooking(tenantId, bookingId);
    }
}
