package pharmacie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numero;

    private LocalDate saisieLe; // Date de saisie
    private LocalDate envoyeeLe; // Date d'envoi

    // Utilisation de BigDecimal pour la précision monétaire (obligatoire en finance)
    @Column(precision = 18, scale = 2) 
    private BigDecimal port;

    @Column(precision = 10, scale = 2)
    private BigDecimal remise;

    @Column(length = 40)
    private String destinataire;

    // Réutilisation de l'objet AdressePostale (Champs ADRESSE, VILLE, etc. dans la table COMMANDE)
    @Embedded
    private AdressePostale adresseLivraison;

    // --- RELATION MANY-TO-ONE : Une commande appartient à un seul Dispensaire ---
    @ManyToOne(optional = false)
    @JoinColumn(name = "DISPENSAIRE_CODE") // Nom de la clé étrangère dans la table COMMANDE
    private Dispensaire dispensaire;

    // --- RELATION ONE-TO-MANY : Une commande contient plusieurs lignes ---
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Toujours exclure les listes du toString pour éviter les boucles infinies !
    private List<Ligne> lignes = new ArrayList<>();
}