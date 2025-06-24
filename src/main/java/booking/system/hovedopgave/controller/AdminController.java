package booking.system.hovedopgave.controller;

import booking.system.hovedopgave.dto.AdminSummaryResponse;
import booking.system.hovedopgave.model.Admin;
import booking.system.hovedopgave.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping
    public Admin createAdmin(@RequestBody Admin admin) {
        return adminService.saveAdmin(admin);
    }

    @GetMapping("/{id}")
    public Admin getAdminById(@PathVariable Long id) {
        return adminService.getAdminById(id);
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

