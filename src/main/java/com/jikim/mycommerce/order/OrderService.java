package com.jikim.mycommerce.order;

import com.jikim.mycommerce.product.Product;
import com.jikim.mycommerce.product.ProductRepository;
import com.jikim.mycommerce.product.ProductService;
import com.jikim.mycommerce.user.User;
import com.jikim.mycommerce.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OrderService
 *
 * 주문 서비스
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;

    /**
     * 주문을 생성한다
     * 
     * 2-Phase 검증 전략:
     * Phase 1: 모든 상품 재고 사전 검증 (빠른 실패)
     * Phase 2: 재고 차감 (분산 락 + DB 락)
     * Phase 3: 주문 생성 및 결제
     *
     * @param userId 사용자 ID
     * @param request 주문 생성 요청
     * @return 생성된 주문
     */
    @Transactional
    public Order createOrder(Long userId, OrderCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // ===== Phase 1: 사전 검증 (빠른 실패) =====
        List<Long> productIds = request.items().stream()
                .map(OrderItemRequest::productId)
                .toList();
        
        Map<Long, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 1-1. 모든 상품 존재 여부 검증
        for (OrderItemRequest item : request.items()) {
            if (!productMap.containsKey(item.productId())) {
                throw new IllegalArgumentException("Product not found: " + item.productId());
            }
        }

        // 1-2. 모든 상품 재고 사전 검증 (락 없이 빠르게)
        for (OrderItemRequest item : request.items()) {
            Product product = productMap.get(item.productId());
            if (product.getStock() < item.quantity()) {
                throw new IllegalArgumentException(
                    String.format("재고 부족: 상품 ID=%d, 요청=%d, 현재 재고=%d", 
                        product.getId(), item.quantity(), product.getStock())
                );
            }
        }

        // ===== Phase 2: 재고 차감 (분산 락 사용) =====
        for (OrderItemRequest item : request.items()) {
            productService.decreaseStockWithLock(item.productId(), item.quantity());
        }

        // ===== Phase 3: 주문 생성 =====
        // 3-1. 총 금액 계산
        BigDecimal totalPrice = request.items().stream()
            .map(item -> {
                Product product = productMap.get(item.productId());
                return product.getPrice().multiply(BigDecimal.valueOf(item.quantity()));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3-2. 주문 생성
        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .build();

        // 3-3. 주문 항목 추가
        for (OrderItemRequest item : request.items()) {
            Product product = productMap.get(item.productId());
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .price(product.getPrice())  // 주문 당시 가격 저장
                    .quantity(item.quantity())
                    .build();
            order.addOrderItem(orderItem);
        }

        // 3-4. 결제 처리 (mock)
        order.processPayment();

        return orderRepository.save(order);
    }

    /**
     * 주문을 조회한다
     *
     * @param id 주문 ID
     * @return 주문
     */
    public Order findOrderById(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }

    /**
     * 사용자의 모든 주문을 조회한다
     *
     * @param userId 사용자 ID
     * @return 주문 목록
     */
    public List<Order> findUserOrders(Long userId) {
        return orderRepository.findAllByUserIdWithItems(userId);
    }

    /**
     * 주문을 취소한다
     * 재고 복원 로직은 TODO로 남겨둠
     *
     * @param id 주문 ID
     * @param userId 사용자 ID
     */
    @Transactional
    public void cancelOrder(Long id, Long userId) {
        Order order = findOrderById(id);

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized: User does not own this order");
        }

        // TODO: 주문 취소 로직 구현
        // 1. 주문 상태 검증 (PENDING, PAID만 취소 가능)
        // 2. 재고 복원
        // 3. 환불 처리 (결제 완료된 경우)
        // 4. 상태를 CANCELLED로 변경

        order.changeStatus(OrderStatus.CANCELLED);
    }
}
