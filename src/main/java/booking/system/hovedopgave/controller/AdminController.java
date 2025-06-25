package booking.system.hovedopgave.controller;

import booking.system.hovedopgave.dto.AdminRequest;
import booking.system.hovedopgave.dto.AdminSummaryResponse;
import booking.system.hovedopgave.model.Admin;
import booking.system.hovedopgave.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<AdminSummaryResponse> createAdmin(@Valid @RequestBody AdminRequest request) {
        AdminSummaryResponse created = adminService.createAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/auth-check")
    public ResponseEntity<String> authCheck() {
        String adminName = adminService.getCurrentAuthenticatedAdminName();
        return ResponseEntity.ok(adminName);
    }

    @GetMapping("/names")
    public ResponseEntity<List<AdminSummaryResponse>> getAdminNames() {
        List<AdminSummaryResponse> summaries = adminService.getAllAdminSummaries();
        return ResponseEntity.ok(summaries);
    }

}

