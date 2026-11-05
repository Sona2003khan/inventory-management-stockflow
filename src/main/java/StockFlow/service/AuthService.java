package StockFlow.service;

import StockFlow.model.Organization;
import StockFlow.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private OrganizationRepository organizationRepository;

    public Organization registerOrganization(String name, String email, String password) {
        if (organizationRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email is already registered!");
        }
        Organization org = new Organization();
        org.setName(name);
        org.setEmail(email);
        org.setPassword(password); // Plain text or hashed for MVP
        return organizationRepository.save(org);
    }

    public Organization login(String email, String password) {
        Optional<Organization> orgOpt = organizationRepository.findByEmail(email);
        if (orgOpt.isPresent() && orgOpt.get().getPassword().equals(password)) {
            return orgOpt.get();
        }
        return null; // Invalid credentials
    }
}