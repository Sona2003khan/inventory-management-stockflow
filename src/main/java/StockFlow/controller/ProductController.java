package StockFlow.controller;

import StockFlow.model.Product;
import StockFlow.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/products/new")
    public String addProductPage(HttpSession session, Model model) {
        if (session.getAttribute("orgId") == null) return "redirect:/login";
        model.addAttribute("product", new Product());
        return "product-form";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductPage(@PathVariable Long id, HttpSession session, Model model) {
        Long orgId = (Long) session.getAttribute("orgId");
        if (orgId == null) return "redirect:/login";

        Product product = productRepository.findByIdAndOrganizationId(id, orgId).orElse(null);
        if (product == null) return "redirect:/dashboard";

        model.addAttribute("product", product);
        return "product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(
            @RequestParam(required = false) Long id,
            @RequestParam String name,
            @RequestParam String sku,
            @RequestParam(required = false) String description,
            @RequestParam Integer quantityOnHand,
            @RequestParam(required = false) Double costPrice,
            @RequestParam(required = false) Double sellingPrice,
            @RequestParam(required = false) Integer lowStockThreshold,
            HttpSession session) {
        
        Long orgId = (Long) session.getAttribute("orgId");
        if (orgId == null) return "redirect:/login";

        Product product;
        if (id != null) {
            // Editing existing product
            product = productRepository.findByIdAndOrganizationId(id, orgId).orElse(new Product());
            product.setUpdatedAt(LocalDateTime.now());
        } else {
            // Creating new product
            product = new Product();
            product.setOrganizationId(orgId);
            product.setCreatedAt(LocalDateTime.now());
        }

        product.setName(name);
        product.setSku(sku);
        product.setDescription(description);
        product.setQuantityOnHand(quantityOnHand);
        product.setCostPrice(costPrice);
        product.setSellingPrice(sellingPrice);
        product.setLowStockThreshold(lowStockThreshold);

        productRepository.save(product);
        return "redirect:/dashboard";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        Long orgId = (Long) session.getAttribute("orgId");
        if (orgId == null) return "redirect:/login";

        productRepository.findByIdAndOrganizationId(id, orgId).ifPresent(productRepository::delete);
        return "redirect:/dashboard";
    }
}