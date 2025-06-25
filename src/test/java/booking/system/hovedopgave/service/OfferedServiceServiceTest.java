package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.OfferedServiceRequest;
import booking.system.hovedopgave.dto.OfferedServiceResponse;
import booking.system.hovedopgave.exception.OfferedServiceException;
import booking.system.hovedopgave.model.Admin;
import booking.system.hovedopgave.repository.AdminRepository;
import booking.system.hovedopgave.security.AdminDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OfferedServiceServiceTest {

    // Injecting the service responsible for managing offered services
    @Autowired
    private OfferedServiceService offeredServiceService;

    // Injecting the repository to access admin users for authentication setup
    @Autowired
    private AdminRepository adminRepository;

    /**
     * Setup method to configure the security context with an authenticated admin user
     * before each test runs. This simulates a logged-in user for security-dependent methods.
     */
    @BeforeEach
    public void setupAuthentication() {
        Admin admin = adminRepository.findByEmail("daniel@email.com").orElseThrow();
        AdminDetails adminDetails = new AdminDetails(admin);

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                adminDetails, null, adminDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /**
     * Happy path test: verifies that creating a valid offered service succeeds
     * and returns the expected details.
     */
    @Test
    public void testCreateValidService_WithAuthentication() {
        OfferedServiceRequest offeredServiceRequest = new OfferedServiceRequest(
                "Aromatherapy Massage",
                "Relaxing essential oil massage",
                300.0
        );

        OfferedServiceResponse service = offeredServiceService.createService(offeredServiceRequest);

        assertNotNull(service.id());
        assertEquals("Aromatherapy Massage", service.name());
        assertEquals(300.0, service.price());
    }

    /**
     * Failure path test: verifies that creating an offered service with a duplicate name
     * fails by throwing an OfferedServiceException.
     */
    @Test
    public void testCreateServiceFailsWithDuplicateName_WithAuthentication() {
        // First service creation should succeed
        OfferedServiceRequest offeredServiceRequest1 = new OfferedServiceRequest(
                "Hot Stone Massage",
                "Warm stones and massage therapy",
                400.0
        );
        offeredServiceService.createService(offeredServiceRequest1);

        // Second service with same name should fail
        OfferedServiceRequest offeredServiceRequest2 = new OfferedServiceRequest(
                "Hot Stone Massage",
                "Another description",
                350.0
        );

        Exception exception = assertThrows(OfferedServiceException.class, () -> {
            offeredServiceService.createService(offeredServiceRequest2);
        });

        assertTrue(exception.getMessage().contains("already exists"), "Should fail due to duplicate service name");
    }

    /**
     * Edge case test: verifies that creating an offered service with a negative price
     * fails by throwing an OfferedServiceException.
     */
    @Test
    public void testCreateServiceFailsWithNegativePrice_WithAuthentication() {
        OfferedServiceRequest offeredServiceRequest = new OfferedServiceRequest(
                "Deep Tissue Massage",
                "Intensive muscle relief",
                -100.0
        );

        Exception exception = assertThrows(OfferedServiceException.class, () -> {
            offeredServiceService.createService(offeredServiceRequest);
        });

        assertTrue(exception.getMessage().toLowerCase().contains("price"), "Should fail due to negative price");
    }
}
