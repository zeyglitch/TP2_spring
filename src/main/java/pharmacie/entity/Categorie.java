package pharmacie.entity;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Categorie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE)
	private Integer code;

	@NonNull
	@Size(min = 1, max = 255)
	@Column(unique = true, length = 255)
	@NotBlank
	private String libelle;

	@Size(max = 255)
	@Column(length = 255)
	private String description;

	@ToString.Exclude
	@OneToMany(mappedBy = "categorie", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private List<Medicament> medicaments = new LinkedList<>();
	
	@PreRemove
	private void checkMedicamentsBeforeRemoval() {
		// Force l'initialisation de la collection si elle est lazy
		// Avec EAGER fetch, la collection devrait toujours être initialisée
		if (medicaments != null && !medicaments.isEmpty()) {
			throw new IllegalStateException("Cannot delete category with associated medications");
		}
	}
	
	public void addMedicament(Medicament medicament) {
		if (!medicaments.contains(medicament)) {
			medicaments.add(medicament);
			medicament.setCategorie(this);
		}
	}
	
	public void removeMedicament(Medicament medicament) {
		medicaments.remove(medicament);
		medicament.setCategorie(null);
	}

}
