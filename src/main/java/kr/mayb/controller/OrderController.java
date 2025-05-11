package kr.mayb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.mayb.dto.OrderInfo;
import kr.mayb.dto.OrderRequest;
import kr.mayb.dto.ProductSimple;
import kr.mayb.enums.PaymentStatus;
import kr.mayb.facade.OrderFacade;
import kr.mayb.security.DenyAll;
import kr.mayb.security.PermitAdmin;
import kr.mayb.security.PermitAuthenticated;
import kr.mayb.util.request.PageRequest;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.PageResponse;
import kr.mayb.util.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order", description = "주문 관련 API")
@DenyAll
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    @Operation(summary = "주문하기")
    @PermitAuthenticated
    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<OrderInfo>> makeOrder(@RequestBody OrderRequest request) {
        OrderInfo response = orderFacade.makeOrder(request);
        return Responses.ok(response);
    }

    @Operation(summary = "내 주문 목록 조회")
    @PermitAuthenticated
    @GetMapping("/orders/me")
    public ResponseEntity<ApiResponse<PageResponse<OrderInfo, Void>>> getMyOrders(PageRequest pageRequest) {
        PageResponse<OrderInfo, Void> response = orderFacade.getMyOrders(pageRequest);
        return Responses.ok(response);
    }

    @Operation(summary = "관리자 전체 주문 목록 조회")
    @PermitAdmin
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<PageResponse<OrderInfo, List<ProductSimple>>>> getOrders(
            @RequestParam(value = "pid", required = false) Long productId,
            @RequestParam(value = "p_status", required = false) PaymentStatus paymentStatus,
            PageRequest pageRequest
    ) {
        var response = orderFacade.getOrders(productId, paymentStatus, pageRequest);
        return Responses.ok(response);
    }
}
