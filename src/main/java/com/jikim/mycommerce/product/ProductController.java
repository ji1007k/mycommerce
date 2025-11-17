package com.jikim.mycommerce.product;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * ProductController
 *
 * 상품 API
 *
 * @author wjddl
 * @since 25. 11. 6.
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 상품을 생성한다
     *
     * @param request 상품 생성 요청
     * @return 생성된 상품 응답
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request)
            throws URISyntaxException {
        Product product = productService.createProduct(request);
        return ResponseEntity
                .created(new URI("/products/" + product.getId()))
                .body(ProductResponse.from(product));
    }

    /**
     * 상품을 조회한다
     *
     * @param id 상품 ID
     * @return 상품 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        Product product = productService.findProductById(id);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    /**
     * 모든 상품을 조회한다
     *
     * @return 상품 목록 응답
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.findAllProducts();
        return ResponseEntity.ok(
                products.stream()
                        .map(ProductResponse::from)
                        .toList()
        );
    }

    /**
     * 상품 정보를 수정한다
     *
     * @param id 상품 ID
     * @param request 상품 수정 요청
     * @return 수정된 상품 응답
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        Product product = productService.updateProduct(id, request);
        return ResponseEntity.ok(ProductResponse.from(product));
    }

    /**
     * 상품을 삭제한다
     *
     * @param id 상품 ID
     * @return 응답 없음
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
