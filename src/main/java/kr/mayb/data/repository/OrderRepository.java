package kr.mayb.data.repository;

import kr.mayb.data.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Page<Order> findAllByMemberId(long memberId, Pageable pageable);

    Optional<Order> findByIdAndMemberId(long orderId, long memberId);
}
