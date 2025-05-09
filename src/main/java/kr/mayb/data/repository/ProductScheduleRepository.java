package kr.mayb.data.repository;

import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductScheduleRepository extends JpaRepository<ProductSchedule, Long> {
    void deleteByProduct(Product product);
}
