package kr.mayb.data.repository;

import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductGenderPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductGenderPriceRepository extends JpaRepository<ProductGenderPrice, Long> {
    void deleteByProduct(Product product);

    Optional<ProductGenderPrice> findByIdAndProduct(long id, Product product);
}
