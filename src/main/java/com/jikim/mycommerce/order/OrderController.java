package com.jikim.mycommerce.order;

import com.jikim.mycommerce.auth.CustomOAuth2User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * OrderController
 *
 * 주문 API
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문을 생성한다
     *
     * @param request 주문 생성 요청
     * @param user 인증된 사용자
     * @return 생성된 주문 응답
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody OrderCreateRequest request,
            @AuthenticationPrincipal CustomOAuth2User user) throws URISyntaxException {

        if (user == null) {
            throw new IllegalStateException("User not authenticated");
        }

        Order order = orderService.createOrder(user.getUserId(), request);
        return ResponseEntity
                .created(new URI("/orders/" + order.getId()))
                .body(OrderResponse.from(order));
    }

    /**
     * 주문을 조회한다
     *
     * @param id 주문 ID
     * @param user 인증된 사용자
     * @return 주문 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomOAuth2User user) {

        // TODO: 권한 검증 - 주문한 사용자만 조회 가능
        Order order = orderService.findOrderById(id);
        return ResponseEntity.ok(OrderResponse.from(order));
    }

    /**
     * 현재 사용자의 모든 주문을 조회한다
     *
     * @param user 인증된 사용자
     * @return 주문 목록 응답
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (user == null) {
            throw new IllegalStateException("User not authenticated");
        }

        List<Order> orders = orderService.findUserOrders(user.getUserId());
        return ResponseEntity.ok(
                orders.stream()
                        .map(OrderResponse::from)
                        .toList()
        );
    }

    /**
     * 주문을 취소한다
     *
     * @param id 주문 ID
     * @param user 인증된 사용자
     * @return 응답 없음
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomOAuth2User user) {

        if (user == null) {
            throw new IllegalStateException("User not authenticated");
        }

        orderService.cancelOrder(id, user.getUserId());
        return ResponseEntity.noContent().build();
    }
}
