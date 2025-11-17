package com.jikim.mycommerce.order;

public enum OrderStatus {
    PENDING,        // 주문 대기 (결제 전)
    PAID,           // 주문 확정 (결제 완료)
    SHIPPING,       // 배송 중
    COMPLETED,      // 배송 완료
    CANCELLED       // 주문 취소
}
