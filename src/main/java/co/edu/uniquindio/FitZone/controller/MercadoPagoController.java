package co.edu.uniquindio.FitZone.controller;

import co.edu.uniquindio.FitZone.service.interfaces.IMembershipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador para manejar las notificaciones de webhooks de Mercado Pago.
 * Este endpoint es llamado por Mercado Pago para notificar cambios en el estado de los pagos.
 */
@RestController
@RequestMapping("/mercadopago")
public class MercadoPagoController {

    private static final Logger logger = LoggerFactory.getLogger(MercadoPagoController.class);

    private final IMembershipService membershipService;

     public MercadoPagoController(IMembershipService membershipService) {
        this.membershipService = membershipService;
     }

    @PostMapping("/notifications")
    public ResponseEntity<Void> handleWebhookNotification(@RequestParam Map<String, String> params) {
        logger.info("Webhook de Mercado Pago recibido. Datos: {}", params);

        try {
            String topic = params.get("topic");
            String paymentId = params.get("data.id");

            if ("payment".equals(topic) && paymentId != null) {
                logger.info("Notificación de pago recibida para el ID: {}", paymentId);
                // ✅ Llamar al servicio para procesar la notificación de pago
                membershipService.processMercadoPagoNotification(paymentId);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error al procesar la notificación del webhook de Mercado Pago", e);
            return ResponseEntity.internalServerError().build();
        }
    }


}
