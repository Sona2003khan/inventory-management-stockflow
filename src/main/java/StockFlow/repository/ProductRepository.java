package StockFlow.repository;

import StockFlow.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByOrganizationId(Long organizationId);
    Optional<Product> findByIdAndOrganizationId(Long id, Long organizationId);

    @Query("SELECT p FROM Product p WHERE p.organizationId = :orgId AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchProducts(@Param("orgId") Long orgId, @Param("keyword") String keyword);
}