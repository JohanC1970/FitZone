package co.edu.uniquindio.FitZone.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sedes")
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSede;
}
