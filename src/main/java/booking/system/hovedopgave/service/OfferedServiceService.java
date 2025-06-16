package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.OfferedServiceRequest;
import booking.system.hovedopgave.dto.OfferedServiceResponse;
import booking.system.hovedopgave.exception.OfferedServiceException;
import booking.system.hovedopgave.model.OfferedService;
import booking.system.hovedopgave.repository.OfferedServiceRepository;
import booking.system.hovedopgave.repository.TimeSlotRepository;
import booking.system.hovedopgave.security.AdminDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public OfferedServiceResponse createService(OfferedServiceRequest offeredServiceRequest) {
        try {
            validateServiceRequest(offeredServiceRequest);

            OfferedService offeredService = new OfferedService();
            offeredService.setName(offeredServiceRequest.name());
            offeredService.setDescription(offeredServiceRequest.description());
            offeredService.setPrice(offeredServiceRequest.price());
            offeredServiceRepository.save(offeredService);

            return toDto(offeredService);

        } catch (Exception e) {
            throw new OfferedServiceException("Service creation failed: " + e.getMessage() , e);
        }
    }

    //Had to use the TimeSlotRepository directly to avoid circular dependency issues
    public void deleteService(Long id) {
        if (!offeredServiceRepository.existsById(id)) {
            throw new OfferedServiceException("Service with ID " + id + " does not exist");
        }
        if (timeSlotRepository.existsByOfferedServiceId(id)) {
            throw new OfferedServiceException("Cannot delete service with ID " + id + " because it has associated time slots");
        }
        offeredServiceRepository.deleteById(id);
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
                offeredService.getPrice()
        );
    }
}


