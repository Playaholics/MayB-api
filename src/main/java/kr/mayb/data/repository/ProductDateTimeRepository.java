package kr.mayb.data.repository;

import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDateTimeRepository extends JpaRepository<ProductDateTime, Long> {
    void deleteByProduct(Product product);
}
