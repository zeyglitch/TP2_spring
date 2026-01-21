package pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Medicament {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer reference = null;

	@NonNull
	@Column(unique = true, length = 255)
	private String nom;

	private String quantiteParUnite = "Une boîte de 12";

	@PositiveOrZero
	private BigDecimal prixUnitaire = BigDecimal.TEN;

	@ToString.Exclude
	@PositiveOrZero
	private int unitesEnStock = 0;

	@ToString.Exclude
	@PositiveOrZero
	private int unitesCommandees = 0;

	@ToString.Exclude
	@PositiveOrZero
	private int niveauDeReappro = 0;

	@ToString.Exclude
	private boolean indisponible = false;

	@Column(length = 500)
	private String imageURL;

	@ManyToOne(optional = true, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@ToString.Exclude
	@Getter
	@Setter
	private Categorie categorie;

}
