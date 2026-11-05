package StockFlow.controller;

import StockFlow.model.Organization;
import StockFlow.model.Product;
import StockFlow.repository.OrganizationRepository;
import StockFlow.repository.ProductRepository;
import StockFlow.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @GetMapping("/")
    public String landing() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String email, @RequestParam String password, HttpSession session, Model model) {
        Organization org = authService.login(email, password);
        if (org != null) {
            session.setAttribute("orgId", org.getId());
            session.setAttribute("orgName", org.getName());
            return "redirect:/dashboard";
        }
        model.addAttribute("error", "Invalid email or password");
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String handleSignup(@RequestParam String name, @RequestParam String email, @RequestParam String password, Model model) {
        try {
            authService.registerOrganization(name, email, password);
            return "redirect:/login?success";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String keyword, HttpSession session, Model model) {
        Long orgId = (Long) session.getAttribute("orgId");
        if (orgId == null) {
            return "redirect:/login";
        }

        List<Product> products;
        if (keyword != null && !keyword.isEmpty()) {
            products = productRepository.searchProducts(orgId, keyword);
        } else {
            products = productRepository.findByOrganizationId(orgId);
        }

        int totalProducts = products.size();
        int totalQuantity = products.stream().mapToInt(p -> p.getQuantityOnHand() != null ? p.getQuantityOnHand() : 0).sum();
        
        Organization org = organizationRepository.findById(orgId).orElse(null);
        int orgDefaultThreshold = org != null ? org.getDefaultLowStockThreshold() : 5;

        long lowStockCount = products.stream().filter(p -> {
            int threshold = p.getLowStockThreshold() != null ? p.getLowStockThreshold() : orgDefaultThreshold;
            return p.getQuantityOnHand() <= threshold;
        }).count();

        model.addAttribute("orgName", session.getAttribute("orgName"));
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalQuantity", totalQuantity);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);

        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}