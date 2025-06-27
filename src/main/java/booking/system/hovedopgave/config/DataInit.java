package booking.system.hovedopgave.config;

import booking.system.hovedopgave.model.*;
import booking.system.hovedopgave.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInit implements CommandLineRunner {

    private final AdminRepository adminRepo;
    private final OfferedServiceRepository offeredServiceRepo;
    private final TimeSlotRepository timeSlotRepo;
    private final BookingRepository bookingRepo;
    private final PasswordEncoder passwordEncoder;

    public DataInit(
            AdminRepository adminRepo,
            OfferedServiceRepository offeredServiceRepo,
            TimeSlotRepository timeSlotRepo,
            BookingRepository bookingRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.adminRepo = adminRepo;
        this.offeredServiceRepo = offeredServiceRepo;
        this.timeSlotRepo = timeSlotRepo;
        this.bookingRepo = bookingRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        Admin admin1 = new Admin();
        admin1.setName("John Doe");
        admin1.setEmail("yogamaster@email.com");
        admin1.setPassword(passwordEncoder.encode("secret123"));
        admin1 = adminRepo.save(admin1);

        Admin admin2 = new Admin();
        admin2.setName("Jane Smith");
        admin2.setEmail("test@email.com");
        admin2.setPassword(passwordEncoder.encode("test123"));
        admin2 = adminRepo.save(admin2);

        Admin admin3 = new Admin();
        admin3.setName("Alice Johnson");
        admin3.setEmail("daniel@email.com");
        admin3.setPassword(passwordEncoder.encode("daniel123"));
        admin3 = adminRepo.save(admin3);

        Admin[] admins = {admin1, admin2, admin3};

        LocalDateTime startDate = LocalDateTime.now()
                .plusDays(1)
                .withHour(10).withMinute(0);

        for (int i = 0; i < admins.length; i++) {
            Admin admin = admins[i];

            OfferedService privateService = new OfferedService();
            privateService.setName("Private Session");
            privateService.setDescription("1-on-1 personalized yoga class");
            privateService.setPrice(300.0);
            privateService.setAdmin(admin);
            privateService = offeredServiceRepo.save(privateService);

            OfferedService groupService = new OfferedService();
            groupService.setName("Group Session");
            groupService.setDescription("Yoga class for up to 5 participants");
            groupService.setPrice(150.0);
            groupService.setAdmin(admin);
            groupService = offeredServiceRepo.save(groupService);

            List<TimeSlot> privateSlots = new ArrayList<>();
            List<TimeSlot> groupSlots = new ArrayList<>();

            for (int j = 0; j < 7; j++) {
                LocalDateTime day = startDate.plusDays(j);

                TimeSlot privateSlot = new TimeSlot();
                privateSlot.setStartTime(day);
                privateSlot.setEndTime(day.plusHours(1));
                privateSlot.setOfferedService(privateService);
                privateSlot.setLocation("123 Yoga Street");
                privateSlot.setMaxParticipants(1);
                privateSlot.setIsAvailable(true);
                privateSlot = timeSlotRepo.save(privateSlot);
                privateSlots.add(privateSlot);

                TimeSlot groupSlot = new TimeSlot();
                groupSlot.setStartTime(day.plusHours(2));
                groupSlot.setEndTime(day.plusHours(3));
                groupSlot.setOfferedService(groupService);
                groupSlot.setLocation("123 Yoga Street");
                groupSlot.setMaxParticipants(5);
                groupSlot.setIsAvailable(true);
                groupSlot = timeSlotRepo.save(groupSlot);
                groupSlots.add(groupSlot);
            }

            Booking booking1 = new Booking();
            booking1.setTimeSlot(privateSlots.get(0));
            booking1.setName("Bob Dylan");
            booking1.setEmail("customer" + (i) + "@mail.com");
            booking1.setPhone("12345678");
            booking1.setStatus(BookingStatus.CONFIRMED);
            booking1.setPaid(true);
            bookingRepo.save(booking1);

            Booking booking2 = new Booking();
            booking2.setTimeSlot(groupSlots.get(0));
            booking2.setName("Mette Frederiksen");
            booking2.setEmail("customer" + (i + 1) + "@mail.com");
            booking2.setPhone("12345678");
            booking2.setStatus(BookingStatus.CONFIRMED);
            booking2.setPaid(true);
            bookingRepo.save(booking2);
        }

        System.out.println("✅ Test data with admins, services, timeslots, and bookings created!");
    }
}
