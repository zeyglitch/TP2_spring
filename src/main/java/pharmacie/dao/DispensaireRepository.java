package pharmacie.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pharmacie.entity.Dispensaire;

// On précise <Dispensaire, String> car la clé primaire (le code) est de type String
public interface DispensaireRepository extends JpaRepository<Dispensaire, String> {

    // Méthode magique demandée : trouver par région
    // Comme "region" est dans l'objet @Embedded "adresse", on concatène les noms : Adresse + Region
    List<Dispensaire> findByAdresseRegion(String region);
}