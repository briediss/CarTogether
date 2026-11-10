package lt.cartogether.repository;

import lt.cartogether.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {

    List<Trip> findByTenantId(Long tenantId);
    Optional<Trip> findByIdAndTenantId(Long id, Long tenantId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Trip t SET t.availableSeats = t.availableSeats - 1 " + "WHERE t.id = :tripId AND t.tenantId = :tenantId AND t.availableSeats > 0")
    int reserveSeat(@Param("tripId") Long tripId, @Param("tenantId") Long tenantId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Trip t SET t.availableSeats = t.availableSeats + 1 " + "WHERE t.id = :tripId AND t.tenantId = :tenantId " + "AND t.availableSeats < t.totalSeats")
    int returnSeat(@Param("tripId") Long tripId, @Param("tenantId") Long tenantId);
}
