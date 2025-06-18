package booking.system.hovedopgave.controller;

import booking.system.hovedopgave.dto.OfferedServiceRequest;
import booking.system.hovedopgave.dto.OfferedServiceResponse;
import booking.system.hovedopgave.service.OfferedServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offered-services")
public class OfferedServiceController {

    private final OfferedServiceService offeredServiceService;

    public OfferedServiceController(OfferedServiceService offeredServiceService) {
        this.offeredServiceService = offeredServiceService;
    }

    @GetMapping
    public ResponseEntity<List<OfferedServiceResponse>> getServicesByAdminId(@RequestParam Long adminId) {
        List<OfferedServiceResponse> responses = offeredServiceService.getServicesByTeacherId(adminId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/admin-services")
    public ResponseEntity<List<OfferedServiceResponse>> getServicesForCurrentAdmin() {
        List<OfferedServiceResponse> responses = offeredServiceService.getServicesForCurrentAdmin();
        return ResponseEntity.ok(responses);
    }

//    @GetMapping
//    public ResponseEntity<List<OfferedServiceResponse>> getAllServices() {
//        List<OfferedServiceResponse> responses = offeredServiceService.getAllServices().stream()
//                .map(offeredServiceService::toDto)
//                .toList();
//        return ResponseEntity.ok(responses);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferedServiceResponse> getServiceById(@PathVariable Long id) {
        return ResponseEntity.ok(
                offeredServiceService.toDto(offeredServiceService.getServiceById(id))
        );
    }

    @PostMapping
    public ResponseEntity<OfferedServiceResponse> createService(@RequestBody OfferedServiceRequest offeredServiceRequest) {
        OfferedServiceResponse created = offeredServiceService.createService(offeredServiceRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        offeredServiceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}


