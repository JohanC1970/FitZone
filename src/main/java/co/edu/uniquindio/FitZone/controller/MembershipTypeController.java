package co.edu.uniquindio.FitZone.controller;

import co.edu.uniquindio.FitZone.dto.request.MembershipTypeRequest;
import co.edu.uniquindio.FitZone.dto.response.MembershipTypeResponse;
import co.edu.uniquindio.FitZone.model.enums.MembershipTypeName;
import co.edu.uniquindio.FitZone.service.interfaces.IMembershipTypeService;
<<<<<<< HEAD
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
=======
>>>>>>> df32851 (Se crearon los controladores para los servicios de Location y MembershipType)
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

<<<<<<< HEAD
    private static final Logger logger = LoggerFactory.getLogger(MembershipTypeController.class);

=======
>>>>>>> df32851 (Se crearon los controladores para los servicios de Location y MembershipType)
    private final IMembershipTypeService membershipTypeService;

    public MembershipTypeController(IMembershipTypeService membershipTypeService) {
        this.membershipTypeService = membershipTypeService;
    }


    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')") // Solo los administradores pueden crear tipos de membresía
<<<<<<< HEAD
    public ResponseEntity<MembershipTypeResponse> createMembershipType(@RequestBody MembershipTypeRequest request) {
        logger.info("POST /membership-types - Creación de tipo de membresía solicitada por administrador");
        logger.debug("Datos de tipo de membresía recibidos - Nombre: {}, Descripción: {}, Precio mensual: {}", 
            request.name(), request.description(), request.monthlyPrice());
        
        try {
            MembershipTypeResponse response = membershipTypeService.createMembershipType(request);
            logger.info("Tipo de membresía creado exitosamente - ID: {}, Nombre: {}, Precio: {}", 
                response.idMembershipType(), response.name(), response.monthlyPrice());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error al crear tipo de membresía - Nombre: {}, Error: {}", 
                request.name(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
=======
    public ResponseEntity<MembershipTypeResponse> createMembershipType(@RequestBody MembershipTypeRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(membershipTypeService.createMembershipType(request));
>>>>>>> df32851 (Se crearon los controladores para los servicios de Location y MembershipType)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')") // Solo los administradores pueden actualizar tipos
    public ResponseEntity<MembershipTypeResponse> updateMembershipType(@PathVariable Long id, @RequestBody MembershipTypeRequest request) {
<<<<<<< HEAD
        logger.info("PUT /membership-types/{} - Actualización de tipo de membresía solicitada por administrador", id);
        logger.debug("Datos de actualización recibidos - Nombre: {}, Descripción: {}, Precio mensual: {}", 
            request.name(), request.description(), request.monthlyPrice());
        
        try {
            MembershipTypeResponse response = membershipTypeService.updateMembershipType(id, request);
            logger.info("Tipo de membresía actualizado exitosamente - ID: {}, Nombre: {}, Precio: {}", 
                response.idMembershipType(), response.name(), response.monthlyPrice());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al actualizar tipo de membresía - ID: {}, Error: {}", 
                id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
=======
        return ResponseEntity.ok(membershipTypeService.updateMembershipType(id, request));
>>>>>>> df32851 (Se crearon los controladores para los servicios de Location y MembershipType)
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembershipTypeResponse> getMembershipTypeById(@PathVariable Long id) {
<<<<<<< HEAD
        logger.debug("GET /membership-types/{} - Consulta de tipo de membresía por ID", id);
        
        try {
            MembershipTypeResponse response = membershipTypeService.getMembershipTypeById(id);
            logger.debug("Tipo de membresía encontrado por ID - ID: {}, Nombre: {}, Precio: {}", 
                id, response.name(), response.monthlyPrice());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al consultar tipo de membresía por ID - ID: {}, Error: {}", 
                id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
=======
        return ResponseEntity.ok(membershipTypeService.getMembershipTypeById(id));
>>>>>>> df32851 (Se crearon los controladores para los servicios de Location y MembershipType)
    }

    @GetMapping("/by-name")
    public ResponseEntity<MembershipTypeResponse> getMembershipTypeByName(@RequestParam MembershipTypeName name) {
<<<<<<< HEAD
        logger.debug("GET /membership-types/by-name - Consulta de tipo de membresía por nombre: {}", name);
        
        try {
            MembershipTypeResponse response = membershipTypeService.getMembershipTypeByName(name);
            logger.debug("Tipo de membresía encontrado por nombre - Nombre: {}, ID: {}, Precio: {}", 
                name, response.idMembershipType(), response.monthlyPrice());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al consultar tipo de membresía por nombre - Nombre: {}, Error: {}", 
                name, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
=======
        return ResponseEntity.ok(membershipTypeService.getMembershipTypeByName(name));
>>>>>>> df32851 (Se crearon los controladores para los servicios de Location y MembershipType)
    }

    @GetMapping
    public ResponseEntity<List<MembershipTypeResponse>> getAllMembershipTypes() {
<<<<<<< HEAD
        logger.debug("GET /membership-types - Consulta de todos los tipos de membresía");
        
        try {
            List<MembershipTypeResponse> membershipTypes = membershipTypeService.getMembershipTypes();
            logger.debug("Se encontraron {} tipos de membresía", membershipTypes.size());
            return ResponseEntity.ok(membershipTypes);
        } catch (Exception e) {
            logger.error("Error al consultar todos los tipos de membresía - Error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
=======
        return ResponseEntity.ok(membershipTypeService.getMembershipTypes());
    }


>>>>>>> df32851 (Se crearon los controladores para los servicios de Location y MembershipType)
}
