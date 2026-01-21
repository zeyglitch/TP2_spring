package pharmacie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numero;

    private LocalDate saisieLe;
    private LocalDate envoyeeLe;

    // Champs ajoutés pour correspondre aux tests
    @Column(length = 50)
    private String nom;
    private Boolean estValidee;

    @Column(precision = 18, scale = 2)
    private BigDecimal port;

    @Column(precision = 10, scale = 2)
    private BigDecimal remise;

    @Column(length = 40)
    private String destinataire;

    @Embedded
    private AdressePostale adresseLivraison;

    @ManyToOne(optional = false)
    @JoinColumn(name = "DISPENSAIRE_CODE")
    private Dispensaire dispensaire;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Ligne> lignes = new ArrayList<>();
}