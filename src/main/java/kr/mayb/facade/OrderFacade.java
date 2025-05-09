package kr.mayb.facade;

import kr.mayb.data.model.Member;
import kr.mayb.data.model.Order;
import kr.mayb.dto.MemberDto;
import kr.mayb.dto.OrderInfo;
import kr.mayb.dto.OrderRequest;
import kr.mayb.dto.OrderedProductItem;
import kr.mayb.service.MemberService;
import kr.mayb.service.OrderService;
import kr.mayb.service.ProductService;
import kr.mayb.util.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final MemberService memberService;
    private final OrderService orderService;
    private final ProductService productService;

    public OrderInfo makeOrder(OrderRequest request) {
        MemberDto member = ContextUtils.loadMember();
        OrderedProductItem productItem = productService.findOrderedProductItem(request.productId(), request.priceId(), request.scheduleId());

        Order saved = orderService.makeOrder(productItem, request.paymentMethod(), member.getMemberId());
        return convertToOrderInfo(saved);
    }

    private OrderInfo convertToOrderInfo(Order saved) {
        Member member = memberService.getMember(saved.getMemberId());
        OrderedProductItem productItem = productService.findOrderedProductItem(saved.getProductId(), saved.getProductGenderPriceId(), saved.getProductScheduleId());

        return OrderInfo.of(saved, productItem, member.getName());
    }
}
