package pharmacie.dao;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import pharmacie.entity.Categorie;
import pharmacie.entity.Commande;
import pharmacie.entity.Dispensaire;
import pharmacie.entity.Medicament;

@DataJpaTest
public class RepositoryCustomMethodsTest {

    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private DispensaireRepository dispensaireRepository;
    @Autowired
    private CommandeRepository commandeRepository;


    @Test // Ce test se base uniquement sur les données définies dans data.sql
    public void testMedicamentCustomMethods() {    
        Medicament indisponible = medicamentRepository.findByNom("Lévofloxacine 500mg").orElseThrow();
        Medicament disponible   = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();
    
        // Trouve tous les médicaments disponibles
        List<Medicament> disponibles = medicamentRepository.findByIndisponibleFalse();

        assertTrue(disponibles.contains(disponible));
        assertFalse(disponibles.contains(indisponible));        
        assertFalse(disponibles.isEmpty());
    }

    @Test // Ce test crée les enregistrements nécessaires
    public void testCategorieCustomMethods() {
        Categorie c1 = new Categorie();
        c1.setLibelle("AnalgesiquesTest");
        categorieRepository.save(c1);

        Categorie c2 = new Categorie();
        c2.setLibelle("AntibiotiquesTest");
        categorieRepository.save(c2);

        // findByLibelle
        Categorie found = categorieRepository.findByLibelle("AnalgesiquesTest");
        assertNotNull(found);
        assertEquals("AnalgesiquesTest", found.getLibelle());

        // findByLibelleContaining
        List<Categorie> list = categorieRepository.findByLibelleContaining("iquesTest");
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(cat -> cat.getLibelle().equals("AntibiotiquesTest")));
        assertTrue(list.stream().anyMatch(cat -> cat.getLibelle().equals("AnalgesiquesTest")));
    }


    @Test
    public void testDispensaireCustomMethods() {
        // On cherche les dispensaires en 'Occitanie'
        List<Dispensaire> occitanie = dispensaireRepository.findByAdresseRegion("Occitanie");
        
        // On doit en trouver 1 (Dispensaire du Sud)
        assertEquals(1, occitanie.size(), "Il devrait y avoir 1 dispensaire en Occitanie");
        assertEquals("Toulouse", occitanie.get(0).getAdresse().getVille());

        // On cherche en 'Ile-de-France' (D02 dans data.sql)
        List<Dispensaire> idf = dispensaireRepository.findByAdresseRegion("Ile-de-France");
        assertEquals(1, idf.size());
        assertEquals("Paris", idf.get(0).getAdresse().getVille());
    }


    @Test
    public void testCommandeCustomMethods() {
        // On cherche les commandes passées APRES le 1er Janvier 2025
        LocalDate datePivot = LocalDate.of(2025, 1, 1);
        List<Commande> commandesRecentes = commandeRepository.findBySaisieLeAfter(datePivot);

        // On ne doit trouver que la commande n°1
        assertEquals(1, commandesRecentes.size(), "Il ne devrait y avoir qu'une seule commande en 2025");
        assertEquals(Integer.valueOf(1), commandesRecentes.get(0).getNumero());
    }

}
