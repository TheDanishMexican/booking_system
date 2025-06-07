package booking.system.hovedopgave.config;

import booking.system.hovedopgave.model.*;
import booking.system.hovedopgave.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInit implements CommandLineRunner {

    private final AdminRepository adminRepo;
    private final OfferedServiceRepository offeredServiceRepo;
    private final TimeSlotRepository timeSlotRepo;
    private final CustomerRepository customerRepo;

    public DataInit(
            AdminRepository adminRepo,
            OfferedServiceRepository offeredServiceRepo,
            TimeSlotRepository timeSlotRepo,
            CustomerRepository customerRepo
    ) {
        this.adminRepo = adminRepo;
        this.offeredServiceRepo = offeredServiceRepo;
        this.timeSlotRepo = timeSlotRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    public void run(String... args) {

        // Admin
        Admin admin = new Admin();
        admin.setEmail("yogamaster@email.com");
        admin.setPassword("secret");
        admin.setName("Anna");
        admin = adminRepo.save(admin);

        // Service
        OfferedService offeredService = new OfferedService();
        offeredService.setName("Private Yoga");
        offeredService.setDescription("1-on-1 session");
        offeredService.setPrice(300.0);
        offeredService = offeredServiceRepo.save(offeredService);

        OfferedService offeredService2 = new OfferedService();
        offeredService2.setName("Group Yoga");
        offeredService2.setDescription("Group session for up to 5 people");
        offeredService2.setPrice(150.0);
        offeredService2 = offeredServiceRepo.save(offeredService2);

        // TimeSlot
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setStartTime(LocalDateTime.of(2025, 6, 1, 10, 0));
        timeSlot.setEndTime(LocalDateTime.of(2025, 6, 1, 11, 0));
        timeSlot.setOfferedService(offeredService);
        timeSlot.setLocation("100 Main Street, Yoga Studio City");
        timeSlot.setMaxParticipants(1);
        timeSlot.setIsAvailable(true);
        timeSlotRepo.save(timeSlot);

        TimeSlot timeSlot2 = new TimeSlot();
        timeSlot2.setStartTime(LocalDateTime.of(2025, 6, 3, 11, 0));
        timeSlot2.setEndTime(LocalDateTime.of(2025, 6, 3, 12, 0));
        timeSlot2.setOfferedService(offeredService2);
        timeSlot2.setLocation("100 Main Street, Yoga Studio City");
        timeSlot2.setMaxParticipants(5);
        timeSlot2.setIsAvailable(true);
        timeSlotRepo.save(timeSlot2);

        // Customer
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("12345678");
        customerRepo.save(customer);

        System.out.println("✅ Admin, Service, TimeSlot, and Customer created.");
    }
}

