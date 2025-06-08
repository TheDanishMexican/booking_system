package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.OfferedServiceRequest;
import booking.system.hovedopgave.exception.OfferedServiceException;
import booking.system.hovedopgave.model.OfferedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OfferedServiceServiceTest {

    @Autowired
    private OfferedServiceService offeredServiceService;

    //Happy path case
    @Test
    public void testCreateValidService() {
        OfferedServiceRequest offeredServiceRequest = new OfferedServiceRequest(
                "Aromatherapy Massage",
                "Relaxing essential oil massage",
                300.0
        );

        OfferedService service = offeredServiceService.createService(offeredServiceRequest);

        assertNotNull(service.getId());
        assertEquals("Aromatherapy Massage", service.getName());
        assertEquals(300.0, service.getPrice());
    }

    //Failure path case
    @Test
    public void testCreateServiceFailsWithDuplicateName() {
        OfferedServiceRequest offeredServiceRequest1 = new OfferedServiceRequest(
                "Hot Stone Massage",
                "Warm stones and massage therapy",
                400.0
        );
        offeredServiceService.createService(offeredServiceRequest1);

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

    //Edge case path
    @Test
    public void testCreateServiceFailsWithNegativePrice() {
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

