package kr.mayb.data.repository;

import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductGenderPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductGenderRepository extends JpaRepository<ProductGenderPrice, Long> {
    void deleteByProduct(Product product);
}
