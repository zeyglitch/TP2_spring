package pharmacie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    @PrePersist
    private void ensureCode() {
        if (this.code == null || this.code.trim().isEmpty()) {
            // generate a short code of max length 5
            this.code = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        }
    }

}