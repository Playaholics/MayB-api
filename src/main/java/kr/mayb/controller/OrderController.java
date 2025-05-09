package kr.mayb.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.mayb.dto.OrderInfo;
import kr.mayb.dto.OrderRequest;
import kr.mayb.facade.OrderFacade;
import kr.mayb.security.DenyAll;
import kr.mayb.security.PermitAuthenticated;
import kr.mayb.util.response.ApiResponse;
import kr.mayb.util.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
}
