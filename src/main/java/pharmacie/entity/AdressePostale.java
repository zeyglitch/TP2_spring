
package pharmacie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdressePostale {

    // On laisse Spring gérer le nom physique "rue" ou on met "adresse" comme
    // demandé
    @Column(name = "ADRESSE", length = 60)
    @Size(max = 60)
    private String rue;

    // Supprimer l'attribut 'name' ici pour éviter le conflit avec le champ
    // 'codePostal'
    @Column(name = "code_postal", length = 10)
    @Size(max = 10)
    private String codePostal; // Sera automatiquement mappé en "code_postal"

    @Column(length = 15)
    @Size(max = 15)
    private String ville;

    @Column(length = 15)
    @Size(max = 15)
    private String pays;

    @Column(length = 15)
    @Size(max = 15)
    private String region;
}