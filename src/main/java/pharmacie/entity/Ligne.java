package pharmacie.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Ligne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "La quantité doit être au moins de 1")
    private Integer quantite;

    // --- RELATION MANY-TO-ONE vers COMMANDE ---
    @ManyToOne(optional = false)
    @JoinColumn(name = "COMMANDE_NUMERO")
    private Commande commande;

    // --- RELATION MANY-TO-ONE vers MEDICAMENT ---
    // Note : On suppose que l'entité Medicament existe déjà dans ton projet
    @ManyToOne(optional = false)
    @JoinColumn(name = "MEDICAMENT_REFERENCE")
    private Medicament medicament;
}