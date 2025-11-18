package com.jikim.mycommerce.fixture;

import com.jikim.mycommerce.order.Order;
import com.jikim.mycommerce.order.OrderItem;
import com.jikim.mycommerce.order.OrderStatus;
import com.jikim.mycommerce.product.Product;
import com.jikim.mycommerce.product.ProductStatus;
import com.jikim.mycommerce.user.User;
import com.jikim.mycommerce.user.UserRole;
import com.jikim.mycommerce.user.UserStatus;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;

/**
 * TestDataFactory
 *
 * 테스트 데이터 생성 팩토리
 * - 매번 새로운 인스턴스 생성
 * - 메서드 체이닝으로 유연한 커스터마이징
 * - 영속성 포함 메서드 제공 (@DataJpaTest용)
 *
 * @author wjddl
 * @since 25. 11. 18.
 */
public class TestDataFactory {

    // ===== User Factory =====
    
    public static UserBuilder user() {
        return new UserBuilder();
    }
    
    public static User defaultUser() {
        return user().build();
    }
    
    public static User userWithEmail(String email) {
        return user().email(email).build();
    }

    public static User adminUser() {
        return user().role(UserRole.ADMIN).build();
    }

    public static class UserBuilder {
        private String name = "테스트유저";
        private String email = "test@example.com";
        private String phoneNumber = "01012345678";
        private String provider = "github";
        private String providerId = "github_123";
        private UserRole role = UserRole.USER;
        private UserStatus status = UserStatus.ACTIVE;

        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public UserBuilder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public UserBuilder providerId(String providerId) {
            this.providerId = providerId;
            return this;
        }

        public UserBuilder role(UserRole role) {
            this.role = role;
            return this;
        }

        public UserBuilder status(UserStatus status) {
            this.status = status;
            return this;
        }

        public User build() {
            return User.builder()
                    .name(name)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .provider(provider)
                    .providerId(providerId)
                    .role(role)
                    .status(status)
                    .build();
        }
    }

    // ===== Product Factory =====
    
    public static ProductBuilder product() {
        return new ProductBuilder();
    }
    
    public static Product defaultProduct() {
        return product().build();
    }
    
    public static Product productWithStock(int stock) {
        return product().stock(stock).build();
    }

    public static Product expensiveProduct() {
        return product().price(50000).name("고가 상품").build();
    }

    public static class ProductBuilder {
        private String name = "테스트상품";
        private String description = "테스트용 상품입니다";
        private BigDecimal price = BigDecimal.valueOf(10000);
        private Integer stock = 100;
        private ProductStatus status = ProductStatus.AVAILABLE;

        public ProductBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ProductBuilder description(String description) {
            this.description = description;
            return this;
        }

        public ProductBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public ProductBuilder price(long price) {
            this.price = BigDecimal.valueOf(price);
            return this;
        }

        public ProductBuilder stock(Integer stock) {
            this.stock = stock;
            return this;
        }

        public ProductBuilder status(ProductStatus status) {
            this.status = status;
            return this;
        }

        public ProductBuilder outOfStock() {
            this.stock = 0;
            this.status = ProductStatus.OUT_OF_STOCK;
            return this;
        }

        public Product build() {
            return Product.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .stock(stock)
                    .status(status)
                    .build();
        }
    }

    // ===== Order Factory =====
    
    public static OrderBuilder order() {
        return new OrderBuilder();
    }
    
    public static Order defaultOrder(User user) {
        return order().user(user).build();
    }

    public static Order pendingOrder(User user) {
        return order().user(user).status(OrderStatus.PENDING).build();
    }

    public static class OrderBuilder {
        private User user;
        private OrderStatus status = OrderStatus.PENDING;
        private BigDecimal totalPrice = BigDecimal.valueOf(10000);

        public OrderBuilder user(User user) {
            this.user = user;
            return this;
        }

        public OrderBuilder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public OrderBuilder totalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        public OrderBuilder totalPrice(long totalPrice) {
            this.totalPrice = BigDecimal.valueOf(totalPrice);
            return this;
        }

        public Order build() {
            if (user == null) {
                throw new IllegalStateException("User는 필수입니다");
            }
            
            return Order.builder()
                    .user(user)
                    .status(status)
                    .totalPrice(totalPrice)
                    .build();
        }
    }

    // ===== OrderItem Factory =====
    
    public static OrderItemBuilder orderItem() {
        return new OrderItemBuilder();
    }

    public static OrderItem defaultOrderItem(Product product) {
        return orderItem().product(product).build();
    }

    public static class OrderItemBuilder {
        private Product product;
        private Integer quantity = 1;
        private BigDecimal price;

        public OrderItemBuilder product(Product product) {
            this.product = product;
            if (this.price == null) {
                this.price = product.getPrice(); // 상품 가격을 기본값으로
            }
            return this;
        }

        public OrderItemBuilder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderItemBuilder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public OrderItemBuilder price(long price) {
            this.price = BigDecimal.valueOf(price);
            return this;
        }

        public OrderItem build() {
            if (product == null) {
                throw new IllegalStateException("Product는 필수입니다");
            }
            if (price == null) {
                price = product.getPrice();
            }
            
            return OrderItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .price(price)
                    .build();
        }
    }

    // ===== 복합 팩토리 메서드 (자주 사용되는 조합) =====
    
    /**
     * 완전한 주문 생성 (Order + OrderItem 포함)
     */
    public static Order completeOrder(User user, Product product, int quantity) {
        BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(quantity));

        Order order = order().user(user)
                .totalPrice(totalPrice)
                .build();
        
        OrderItem orderItem = orderItem()
                .product(product)
                .quantity(quantity)
                .build();
        
        order.addOrderItem(orderItem);
        return order;
    }

    /**
     * 동시성 테스트용 시나리오 데이터
     */
    public static ConcurrencyTestData concurrencyTestData() {
        User user = defaultUser();
        Product product = productWithStock(10); // 재고 10개
        return new ConcurrencyTestData(user, product);
    }

    public record ConcurrencyTestData(User user, Product product) {

    }

    // ===== 영속성 포함 팩토리 메서드 (@DataJpaTest용) =====
    
    /**
     * User 생성 + 저장
     */
    public static User savedUser(TestEntityManager entityManager) {
        return entityManager.persistAndFlush(defaultUser());
    }
    
    /**
     * Builder 활용해서 유연한 User 생성 + 저장
     */
    public static User savedUser(TestEntityManager entityManager, UserBuilder userBuilder) {
        return entityManager.persistAndFlush(userBuilder.build());
    }

    /**
     * Product 생성 + 저장
     */
    public static Product savedProduct(TestEntityManager entityManager) {
        return entityManager.persistAndFlush(defaultProduct());
    }
    
    /**
     * Builder 활용해서 유연한 Product 생성 + 저장
     */
    public static Product savedProduct(TestEntityManager entityManager, ProductBuilder productBuilder) {
        return entityManager.persistAndFlush(productBuilder.build());
    }
    
    /**
     * Builder 활용해서 유연한 Order 생성 + 저장
     */
    public static Order savedOrder(TestEntityManager entityManager, OrderBuilder orderBuilder) {
        return entityManager.persistAndFlush(orderBuilder.build());
    }

    /**
     * 완전한 주문 생성 + 저장 (Order + OrderItem 포함)
     * User, Product는 이미 저장된 상태여야 함
     */
    public static Order savedCompleteOrder(TestEntityManager entityManager, User savedUser, Product savedProduct, int quantity) {
        Order order = completeOrder(savedUser, savedProduct, quantity);
        return entityManager.persistAndFlush(order);
    }
    
    /**
     * 모든 것을 한 번에 생성 + 저장
     */
    public static OrderWithDependencies savedOrderWithDependencies(TestEntityManager entityManager, int quantity) {
        User savedUser = savedUser(entityManager);
        Product savedProduct = savedProduct(entityManager);
        Order savedOrder = savedCompleteOrder(entityManager, savedUser, savedProduct, quantity);
        
        return new OrderWithDependencies(savedUser, savedProduct, savedOrder);
    }

    /**
     * Repository 테스트용 편의 클래스
     */
    public record OrderWithDependencies(User user, Product product, Order order) {

    }
}