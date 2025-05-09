package kr.mayb.data.repository;

import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductGender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductGenderRepository extends JpaRepository<ProductGender, Long> {
    void deleteByProduct(Product product);
}
