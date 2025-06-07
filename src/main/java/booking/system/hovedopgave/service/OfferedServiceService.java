package booking.system.hovedopgave.service;

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
    @Autowired
    private AdminService adminService;


    public List<OfferedService> getAllServices() {
        return offeredServiceRepository.findAll();
    }

    public OfferedService getServiceById(Long id) {
        return offeredServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
    }

    public OfferedService createService(OfferedService service) {
        return offeredServiceRepository.save(service);
    }

    public void deleteService(Long id) {
        OfferedService service = offeredServiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found with id: " + id));
        offeredServiceRepository.delete(service);
    }


}

