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
        offeredService.setAdmin(admin);
        offeredService = offeredServiceRepo.save(offeredService);

        // TimeSlot
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setStartTime(LocalDateTime.of(2025, 6, 1, 10, 0));
        timeSlot.setEndTime(LocalDateTime.of(2025, 6, 1, 11, 0));
        timeSlot.setOfferedService(offeredService);
        timeSlotRepo.save(timeSlot);

        // Customer
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("12345678");
        customerRepo.save(customer);

        System.out.println("✅ Admin, Service, TimeSlot, and Customer created.");
    }
}

