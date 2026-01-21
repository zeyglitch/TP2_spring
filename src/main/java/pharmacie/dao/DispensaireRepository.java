package pharmacie.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
// Bien importer l'entité Dispensaire
import pharmacie.entity.Dispensaire; 


public interface DispensaireRepository extends JpaRepository<Dispensaire, String> {

    // Méthode magique pour trouver par région
    // Le type de retour doit être une liste d'entités (Dispensaire)
    List<Dispensaire> findByAdresseRegion(String region);
}