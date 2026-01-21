package pharmacie.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pharmacie.entity.Medicament;

public interface MedicamentRepository extends JpaRepository<Medicament, Integer> {
        /**
         * Trouve un médicament à partir de son nom (unique dans Medicament)
         * 
         * @return un médicament "optionnel"
         */
        Optional<Medicament> findByNom(String nom);

        /**
         * Trouve les médicaments disponibles (indisponible = false)
         * 
         * @return la liste des médicaments disponibles
         */
        List<Medicament> findByIndisponibleFalse();

        @Query("SELECT ligne.medicament.nom as nom, SUM(ligne.quantite) AS unites "
                        + "FROM Ligne ligne "
                        + "WHERE ligne.medicament.categorie.code = :codeCategorie "
                        + "GROUP BY nom ")
        public List<UnitesParMedicament> medicamentsVendusPour(Integer codeCategorie);

        @Query("""
                        SELECT l.medicament.nom as nom, SUM(l.quantite) AS unites
                        FROM Ligne l
                        WHERE l.medicament.categorie.code = :codeCategorie
                        GROUP BY nom
                        """)
        List<UnitesParMedicament> medicamentsCommandesPour(Integer codeCategorie);

        @Query(value = """
                        SELECT m.nom as nom, SUM(l.quantite) AS unites
                        FROM Categorie c
                        INNER JOIN Medicament m ON c.code = m.categorie_code
                        INNER JOIN Ligne l ON m.reference = l.medicament_reference
                        WHERE c.code = :codeCategorie
                        GROUP BY m.nom
                        """, nativeQuery = true)
        List<UnitesParMedicament> medicamentsCommandesPourNative(Integer codeCategorie);

        @Query("SELECT m FROM Medicament m WHERE m.categorie.code = :codeCategorie AND m.indisponible = false AND m.unitesEnStock >= m.unitesCommandees")
        List<Medicament> findMedicamentsDisponiblesPourCategorie(Integer codeCategorie);

}
