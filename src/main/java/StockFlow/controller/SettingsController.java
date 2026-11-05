package StockFlow.controller;

import StockFlow.model.Organization;
import StockFlow.repository.OrganizationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SettingsController {

    @Autowired
    private OrganizationRepository organizationRepository;

    @GetMapping("/settings")
    public String settingsPage(HttpSession session, Model model) {
        Long orgId = (Long) session.getAttribute("orgId");
        if (orgId == null) return "redirect:/login";

        Organization org = organizationRepository.findById(orgId).orElse(null);
        if (org == null) return "redirect:/login";

        model.addAttribute("org", org);
        return "settings";
    }

    @PostMapping("/settings/save")
    public String updateSettings(@RequestParam int defaultLowStockThreshold, HttpSession session) {
        Long orgId = (Long) session.getAttribute("orgId");
        if (orgId == null) return "redirect:/login";

        Organization org = organizationRepository.findById(orgId).orElse(null);
        if (org != null) {
            org.setDefaultLowStockThreshold(defaultLowStockThreshold);
            organizationRepository.save(org);
        }

        return "redirect:/settings?success";
    }
}