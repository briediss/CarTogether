package lt.cartogether.controller;

import lt.cartogether.model.Trip;
import lt.cartogether.service.BookingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Trip bookTrip(@RequestParam Long tenantId, @RequestParam Long tripId){
        return bookingService.bookTrip(tenantId, tripId);
    }
}
