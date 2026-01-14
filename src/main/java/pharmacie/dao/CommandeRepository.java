package pharmacie.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import pharmacie.entity.Commande;

// <Commande, Integer> car la clé primaire est un Integer
public interface CommandeRepository extends JpaRepository<Commande, Integer> {

    // Méthode magique demandée : trouver les commandes après une date
    // Spring traduit "After" par l'opérateur SQL ">"
    List<Commande> findBySaisieLeAfter(LocalDate date);
}