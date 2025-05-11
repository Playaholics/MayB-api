package kr.mayb.facade;

import kr.mayb.data.model.Member;
import kr.mayb.data.model.Order;
import kr.mayb.dto.*;
import kr.mayb.enums.PaymentStatus;
import kr.mayb.service.MemberService;
import kr.mayb.service.OrderService;
import kr.mayb.service.ProductService;
import kr.mayb.util.ContextUtils;
import kr.mayb.util.request.PageRequest;
import kr.mayb.util.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final MemberService memberService;
    private final OrderService orderService;
    private final ProductService productService;

    private static final String DEFAULT_UNKNOWN_MEMBER_NAME = "탈퇴하거나 찾을 수 없는 회원입니다.";

    public OrderInfo makeOrder(OrderRequest request) {
        MemberDto member = ContextUtils.loadMember();
        OrderedProductItem productItem = productService.findOrderedProductItem(request.productId(), request.priceId(), request.scheduleId());

        Order saved = orderService.makeOrder(productItem, request.paymentMethod(), member.getMemberId());
        return convertToOrderInfo(saved);
    }

    public PageResponse<OrderInfo, Void> getMyOrders(PageRequest pageRequest) {
        MemberDto member = ContextUtils.loadMember();
        Page<Order> orders = orderService.getMyOrders(member.getMemberId(), pageRequest);

        List<OrderInfo> converted = convertToOrderInfos(orders.getContent());
        return PageResponse.of(new PageImpl<>(converted, orders.getPageable(), orders.getTotalElements()));
    }

    public PageResponse<OrderInfo, List<ProductSimple>> getOrders(Long productId, PaymentStatus paymentStatus, PageRequest pageRequest) {
        Page<Order> orders = orderService.getOrders(productId, paymentStatus, pageRequest);

        List<OrderInfo> converted = convertToOrderInfos(orders.getContent());
        PageImpl<OrderInfo> orderInfoPage = new PageImpl<>(converted, orders.getPageable(), orders.getTotalElements());
        return PageResponse.of(orderInfoPage, getProductMetaData());
    }

    public OrderInfo updatePaymentStatus(long orderId, long memberId, PaymentStatus status) {
        Order updated = orderService.updatePaymentStatus(orderId, memberId, status);
        return convertToOrderInfo(updated);
    }

    private List<ProductSimple> getProductMetaData() {
        return productService.findAll()
                .stream()
                .map(ProductSimple::of)
                .toList();
    }

    private List<OrderInfo> convertToOrderInfos(List<Order> orders) {
        Map<Long, Member> memberMap = memberService.findAllByIdIn(orders.stream().map(Order::getMemberId).collect(Collectors.toSet()));
        Set<Long> productIds = orders.stream().map(Order::getProductId).collect(Collectors.toSet());
        Set<Long> priceIds = orders.stream().map(Order::getProductGenderPriceId).collect(Collectors.toSet());
        Set<Long> scheduleIds = orders.stream().map(Order::getProductScheduleId).collect(Collectors.toSet());

        var orderedProductItemMap = productService.findOrderedProductItems(productIds, priceIds, scheduleIds, orders);
        return orders.stream()
                .map(order -> {
                    String customerName = Optional.ofNullable(memberMap.get(order.getMemberId()))
                            .map(Member::getName)
                            .orElse(DEFAULT_UNKNOWN_MEMBER_NAME);
                    OrderedProductItem productItem = orderedProductItemMap.get(order.getId());

                    return OrderInfo.of(order, productItem, customerName);
                })
                .toList();
    }

    private OrderInfo convertToOrderInfo(Order saved) {
        Member member = memberService.getMember(saved.getMemberId());
        OrderedProductItem productItem = productService.findOrderedProductItem(saved.getProductId(), saved.getProductGenderPriceId(), saved.getProductScheduleId());

        return OrderInfo.of(saved, productItem, member.getName());
    }
}
