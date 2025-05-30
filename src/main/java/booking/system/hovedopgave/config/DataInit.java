package booking.system.hovedopgave.config;

import booking.system.hovedopgave.model.*;
import booking.system.hovedopgave.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInit implements CommandLineRunner {

    private final AdminRepository adminRepo;
    private final ServiceRepository serviceRepo;
    private final TimeSlotRepository timeSlotRepo;
    private final CustomerRepository customerRepo;

    public DataInit(
            AdminRepository adminRepo,
            ServiceRepository serviceRepo,
            TimeSlotRepository timeSlotRepo,
            CustomerRepository customerRepo
    ) {
        this.adminRepo = adminRepo;
        this.serviceRepo = serviceRepo;
        this.timeSlotRepo = timeSlotRepo;
        this.customerRepo = customerRepo;
    }

    @Override
    public void run(String... args) {

        // Admin
        Admin admin = new Admin();
        admin.setUsername("yogamaster");
        admin.setPassword("secret");
        admin.setName("Anna");
        admin = adminRepo.save(admin);

        // Service
        Service service = new Service();
        service.setName("Private Yoga");
        service.setDescription("1-on-1 session");
        service.setPrice(300.0);
        service.setAdmin(admin);
        service = serviceRepo.save(service);

        // TimeSlot
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setStartTime(LocalDateTime.of(2025, 6, 1, 10, 0));
        timeSlot.setEndTime(LocalDateTime.of(2025, 6, 1, 11, 0));
        timeSlot.setService(service);
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

