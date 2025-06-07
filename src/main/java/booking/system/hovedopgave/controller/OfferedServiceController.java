package booking.system.hovedopgave.controller;

import booking.system.hovedopgave.model.OfferedService;
import booking.system.hovedopgave.service.OfferedServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offered-services")
public class OfferedServiceController {

    @Autowired
    private OfferedServiceService offeredServiceService;

    @GetMapping
    public List<OfferedService> getAllServices() {
        return offeredServiceService.getAllServices();
    }

    @GetMapping("/{id}")
    public OfferedService getServiceById(@PathVariable Long id) {
        return offeredServiceService.getServiceById(id);
    }

    @PostMapping
    public OfferedService createService(@RequestBody OfferedService offeredService) {
        return offeredServiceService.createService(offeredService);
    }

    @DeleteMapping("/{id}")
    public void deleteService(@PathVariable Long id) {
        offeredServiceService.deleteService(id);
    }

}

