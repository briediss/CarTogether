package lt.cartogether.repository;

import lt.cartogether.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByTenantId(Long tenantId);
    Optional<Trip> findByIdAndTenantId(Long id, Long tenantId);
}
