package lt.cartogether.repository;

import lt.cartogether.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByTenantId(Long tenantId);
    Optional<Booking> findByIdAndTenantId(Long id, Long tenantId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Booking b SET b.cancelled = true " + "WHERE b.id = :bookingId AND b.tenantId = :tenantId " + "AND b.cancelled = false")
    int cancelBooking(@Param("bookingId") Long bookingId, @Param("tenantId") Long tenantId);
}
