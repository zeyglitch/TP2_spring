package pharmacie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Dispensaire {

    @Id
    @Column(length = 5)
    @Size(max = 5, message = "Le code ne doit pas dépasser 5 caractères")
    private String code;

    @Column(length = 40, nullable = false)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 40)
    private String nom;

    // --- Champs de contact ---
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

    // --- L'Adresse (Relation @Embedded) ---
    // JPA va prendre les champs de AdressePostale et les mettre dans la table DISPENSAIRE
    @Embedded
    private AdressePostale adresse;
}