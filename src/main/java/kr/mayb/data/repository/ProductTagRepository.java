package kr.mayb.data.repository;

import kr.mayb.data.model.Product;
import kr.mayb.data.model.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {

    void deleteByProduct(Product product);
}
