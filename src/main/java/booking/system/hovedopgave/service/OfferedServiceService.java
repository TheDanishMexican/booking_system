package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.OfferedServiceRequest;
import booking.system.hovedopgave.dto.OfferedServiceResponse;
import booking.system.hovedopgave.exception.OfferedServiceException;
import booking.system.hovedopgave.model.Admin;
import booking.system.hovedopgave.model.OfferedService;
import booking.system.hovedopgave.repository.OfferedServiceRepository;
import booking.system.hovedopgave.repository.TimeSlotRepository;
import booking.system.hovedopgave.security.AdminDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OfferedServiceService {

    private final OfferedServiceRepository offeredServiceRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final AdminService adminService;

    public OfferedServiceService(OfferedServiceRepository offeredServiceRepository, TimeSlotRepository timeSlotRepository, AdminService adminService) {
        this.offeredServiceRepository = offeredServiceRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.adminService = adminService;
    }

    public List<OfferedServiceResponse> getServicesForCurrentAdmin() {
        Long adminId = adminService.getCurrentAuthenticatedAdminId();
        List<OfferedService> services = offeredServiceRepository.findByAdminId(adminId);
        return services.stream()
                .map(this::toDto)
                .toList();
    }

    public List<OfferedService> getAllServices() {
        return offeredServiceRepository.findAll();
    }

    public OfferedService getServiceById(Long id) {
        return offeredServiceRepository.findById(id)
                .orElseThrow(() -> new OfferedServiceException("Service not found with id: " + id));
    }

    @Transactional
    public OfferedServiceResponse createService(OfferedServiceRequest offeredServiceRequest) {
          Long adminId = adminService.getCurrentAuthenticatedAdminId();
          Admin admin = adminService.getAdminById(adminId);

        try {
            validateServiceRequest(offeredServiceRequest);

            OfferedService offeredService = new OfferedService();
            offeredService.setAdmin(admin);
            offeredService.setName(offeredServiceRequest.name());
            offeredService.setDescription(offeredServiceRequest.description());
            offeredService.setPrice(offeredServiceRequest.price());
            offeredServiceRepository.save(offeredService);

            return toDto(offeredService);

        } catch (Exception e) {
            throw new OfferedServiceException("Service creation failed: " + e.getMessage() , e);
        }
    }

    @Transactional
    //Had to use the TimeSlotRepository directly to avoid circular dependency issues
    public void deleteService(Long id) {
        ensureAdminOwnsService(id);

        if (!offeredServiceRepository.existsById(id)) {
            throw new OfferedServiceException("Service with ID " + id + " does not exist");
        }
        if (timeSlotRepository.existsByOfferedServiceId(id)) {
            throw new OfferedServiceException("Cannot delete service with ID " + id + " because it has associated time slots");
        }
        offeredServiceRepository.deleteById(id);
    }

    private void ensureAdminOwnsService(Long offeredServiceId) {
        Long adminId = adminService.getCurrentAuthenticatedAdminId();
        OfferedService service = getServiceById(offeredServiceId);

        if (!service.getAdmin().getId().equals(adminId)) {
            throw new OfferedServiceException("You do not have permission to access this service");
        }
    }


    public void validateServiceRequest(OfferedServiceRequest request) {
        if (request.price() <= 0) {
            throw new OfferedServiceException("Price must be a positive number");
        }
        if (offeredServiceRepository.existsByName(request.name())) {
            throw new OfferedServiceException("Service with name '" + request.name() + "' already exists");
        }
    }

    public OfferedServiceResponse toDto(OfferedService offeredService) {
        return new OfferedServiceResponse(
                offeredService.getId(),
                offeredService.getName(),
                offeredService.getDescription(),
                offeredService.getPrice(),
                offeredService.getAdmin().getId()
        );
    }

    public List<OfferedServiceResponse> getServicesByTeacherId(Long adminId) {
        List<OfferedService> services = offeredServiceRepository.findByAdminId(adminId);
        if (services.isEmpty()) {
            throw new OfferedServiceException("No services found for admin with ID: " + adminId);
        }
        return services.stream()
                .map(this::toDto)
                .toList();
    }
}


