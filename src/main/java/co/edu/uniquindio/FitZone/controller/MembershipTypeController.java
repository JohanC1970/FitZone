package co.edu.uniquindio.FitZone.controller;

import co.edu.uniquindio.FitZone.dto.request.MembershipTypeRequest;
import co.edu.uniquindio.FitZone.dto.response.MembershipTypeResponse;
import co.edu.uniquindio.FitZone.model.enums.MembershipTypeName;
import co.edu.uniquindio.FitZone.service.interfaces.IMembershipTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar los tipos de membresía.
 * Proporciona endpoints para crear, actualizar y obtener tipos de membresía.
 */
@RestController
@RequestMapping("/membership-types")
public class MembershipTypeController {

    private final IMembershipTypeService membershipTypeService;

    public MembershipTypeController(IMembershipTypeService membershipTypeService) {
        this.membershipTypeService = membershipTypeService;
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')") // Solo los administradores pueden crear tipos de membresía
    public ResponseEntity<MembershipTypeResponse> createMembershipType(@RequestBody MembershipTypeRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(membershipTypeService.createMembershipType(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // Solo los administradores pueden actualizar tipos
    public ResponseEntity<MembershipTypeResponse> updateMembershipType(@PathVariable Long id, @RequestBody MembershipTypeRequest request) {
        return ResponseEntity.ok(membershipTypeService.updateMembershipType(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembershipTypeResponse> getMembershipTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(membershipTypeService.getMembershipTypeById(id));
    }

    @GetMapping("/by-name")
    public ResponseEntity<MembershipTypeResponse> getMembershipTypeByName(@RequestParam MembershipTypeName name) {
        return ResponseEntity.ok(membershipTypeService.getMembershipTypeByName(name));
    }

    @GetMapping
    public ResponseEntity<List<MembershipTypeResponse>> getAllMembershipTypes() {
        return ResponseEntity.ok(membershipTypeService.getMembershipTypes());
    }


}
