package kr.mayb.data.repository;

import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProductScheduleRepository extends JpaRepository<ProductSchedule, Long> {
    void deleteByProduct(Product product);

    Optional<ProductSchedule> findByIdAndProduct(long id, Product product);

    List<ProductSchedule> findAllByIdIn(Collection<Long> scheduleIds);
}
