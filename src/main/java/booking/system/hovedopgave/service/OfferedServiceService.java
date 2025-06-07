package booking.system.hovedopgave.service;

import booking.system.hovedopgave.dto.OfferedServiceRequest;
import booking.system.hovedopgave.exception.OfferedServiceException;
import booking.system.hovedopgave.model.Admin;
import booking.system.hovedopgave.model.OfferedService;
import booking.system.hovedopgave.repository.OfferedServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfferedServiceService {

    @Autowired
    private OfferedServiceRepository offeredServiceRepository;


    public List<OfferedService> getAllServices() {
        return offeredServiceRepository.findAll();
    }

    public OfferedService getServiceById(Long id) {
        return offeredServiceRepository.findById(id)
                .orElseThrow(() -> new OfferedServiceException("Service not found with id: " + id));
    }

    public OfferedService createService(OfferedServiceRequest offeredServiceRequest) {
        try {
            OfferedService offeredService = new OfferedService();
            offeredService.setName(offeredServiceRequest.name());
            offeredService.setDescription(offeredServiceRequest.description());
            offeredService.setPrice(offeredServiceRequest.price());

            return offeredServiceRepository.save(offeredService);

        } catch (Exception e) {
            throw new OfferedServiceException("Service creation failed: ", e);
        }
    }

    public void deleteService(Long id) {
        if (!offeredServiceRepository.existsById(id)) {
            throw new OfferedServiceException("Service with ID " + id + " does not exist");
        }
        offeredServiceRepository.deleteById(id);
    }


}

