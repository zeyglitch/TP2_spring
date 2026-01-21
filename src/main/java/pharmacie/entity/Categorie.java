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

	@OneToMany(mappedBy = "categorie")
	private List<Medicament> medicaments = new LinkedList<>();

}
