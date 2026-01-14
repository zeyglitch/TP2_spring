package pharmacie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable // Indique que cette classe n'est pas une table, mais une partie d'une table
@Data @NoArgsConstructor @AllArgsConstructor
public class AdressePostale {

    // On mappe le champ "rue" de l'UML vers la colonne "ADRESSE" de la BDD
    @Column(name = "ADRESSE", length = 60) 
    @Size(max = 60)
    private String rue;

    @Column(name = "CODE_POSTAL", length = 10)
    @Size(max = 10)
    private String codePostal;

    @Column(length = 15)
    @Size(max = 15)
    private String ville;

    // Ajoutés car présents dans le schéma logique (table) même si absents de l'UML simplifié
    @Column(length = 15)
    @Size(max = 15)
    private String pays;

    @Column(length = 15)
    @Size(max = 15)
    private String region;
}