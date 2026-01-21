package pharmacie.dao;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import pharmacie.entity.Commande; // Import indispensable

public interface CommandeRepository extends JpaRepository<Commande, Integer> {

    List<Commande> findBySaisieLeAfter(LocalDate date);

    Commande findByNom(String nom);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(l.quantite) FROM Commande c JOIN c.lignes l WHERE c.dispensaire.code = :codeDispensaire AND c.envoyeeLe IS NOT NULL")
    Long countArticlesCommandesPourDispensaire(String codeDispensaire);

    List<Commande> findByDispensaireCodeAndEnvoyeeLeIsNull(String codeDispensaire);
}