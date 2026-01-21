package pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Dispensaire {

    @Id
    @Column(length = 5)
    @Size(max = 5)
    private String code;

    @Column(length = 40, nullable = false)
    @NotBlank
    @Size(max = 40)
    private String nom;

    @Column(length = 30)
    @Size(max = 30)
    private String contact;

    @Column(length = 30)
    @Size(max = 30)
    private String fonction;

    @Column(length = 24)
    @Size(max = 24)
    private String telephone;

    @Column(length = 24)
    @Size(max = 24)
    private String fax;

    @Embedded
    private AdressePostale adresse;

    @OneToMany(mappedBy = "dispensaire", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Commande> commandes = new ArrayList<>();

    @PrePersist
    private void ensureCode() {
        if (this.code == null || this.code.trim().isEmpty()) {
            // generate a short code of max length 5
            this.code = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        }
    }
    
    public void addCommande(Commande commande) {
        if (!commandes.contains(commande)) {
            commandes.add(commande);
            commande.setDispensaire(this);
        }
    }
    
    public void removeCommande(Commande commande) {
        commandes.remove(commande);
        commande.setDispensaire(null);
    }

}