package pharmacie.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pharmacie.entity.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RepositoryCustomMethodsTest {

    @Autowired
    private CategorieRepository categorieRepository;
    @Autowired
    private MedicamentRepository medicamentRepository;
    @Autowired
    private CommandeRepository commandeRepository;
    @Autowired
    private LigneRepository ligneRepository;
    @Autowired
    private DispensaireRepository dispensaireRepository;

    @Test // Ce test se base uniquement sur les données définies dans data.sql
    public void testMedicamentCustomMethods() {
        Medicament indisponible = medicamentRepository.findByNom("Lévofloxacine 500mg").orElseThrow();
        Medicament disponible = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();

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
    public void unMedicamentaUneCategorie() {
        Medicament m1 = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();
        Categorie c1 = categorieRepository.findByLibelle("AnalgesiquesTest");
        m1.setCategorie(c1);
        medicamentRepository.save(m1);
        assertEquals(c1, m1.getCategorie());
    }

    @Test
    public void supprimerUneCategorieSansMediacament() {
        Categorie c1 = new Categorie();
        c1.setLibelle("TestDelete");
        categorieRepository.save(c1);
        categorieRepository.delete(c1);
        assertNull(categorieRepository.findByLibelle("TestDelete"));
    }

    @Test
    public void nePasSupprimerUneCategorieAvecMedicament() {
        Categorie cat1 = new Categorie();
        cat1.setLibelle("test");
        categorieRepository.save(cat1);
        Medicament m1 = new Medicament();
        m1.setNom("testsuppr");
        m1.setCategorie(cat1);
        medicamentRepository.save(m1);
        categorieRepository.delete(cat1);
        assertNotNull(categorieRepository.findByLibelle("test"));
    }

    @Test
    public void supprimerUneCommandeAvecToutesSesLignesDeCommande() {
        Dispensaire d1 = new Dispensaire();
        d1.setNom("DispensaireTest");
        AdressePostale adr = new AdressePostale();
        adr.setRue("rue test");
        adr.setCodePostal("12345");
        adr.setVille("VilleTest");
        adr.setPays("PaysTest");
        adr.setRegion("RegionTest");
        d1.setAdresse(adr);
        dispensaireRepository.save(d1);

        Commande c1 = new Commande();
        c1.setNom("renouvelement");
        c1.setSaisieLe(LocalDate.now());
        c1.setEstValidee(false);
        c1.setDispensaire(d1);
        commandeRepository.save(c1);
        Ligne lc1 = new Ligne();
        lc1.setCommande(c1);
        lc1.setMedicament(medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow());
        lc1.setQuantite(1);
        ligneRepository.save(lc1);
        Ligne lc2 = new Ligne();
        lc2.setCommande(c1);
        lc2.setMedicament(medicamentRepository.findByNom("Lévofloxacine 500mg").orElseThrow());
        lc2.setQuantite(2);
        ligneRepository.save(lc2);

        commandeRepository.delete(c1);
        assertNull(commandeRepository.findByNom("renouvelement"));

    }

    @Test
    public void supprimerUnDispensaireEtSesCommandes() {
        Dispensaire d1 = new Dispensaire();
        d1.setNom("test");
        AdressePostale adr = new AdressePostale();
        adr.setRue("test");
        adr.setCodePostal("test");
        adr.setVille("test");
        adr.setPays("test");
        adr.setRegion("test");
        d1.setAdresse(adr);
        d1.setTelephone("test");
        dispensaireRepository.save(d1);
        Commande c1 = new Commande();
        c1.setNom("renouvelement");
        c1.setSaisieLe(LocalDate.now());
        c1.setEstValidee(false);
        c1.setDispensaire(d1);
        commandeRepository.save(c1);
        Ligne lc1 = new Ligne();
        lc1.setCommande(c1);
        lc1.setMedicament(medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow());
        lc1.setQuantite(1);
        ligneRepository.save(lc1);
        Ligne lc2 = new Ligne();
        lc2.setCommande(c1);
        lc2.setMedicament(medicamentRepository.findByNom("Lévofloxacine 500mg").orElseThrow());
        lc2.setQuantite(2);
        ligneRepository.save(lc2);
        commandeRepository.delete(c1);
        assertNull(commandeRepository.findByNom("renouvelement"));

    }

    @Test
    public void testCountArticlesCommandesPourDispensaire() {
        Dispensaire d1 = new Dispensaire();
        d1.setCode("DTEST");
        d1.setNom("DispTestItems");
        d1.setAdresse(new AdressePostale("rue", "12345", "Ville", "Pays", "Region"));
        dispensaireRepository.save(d1);

        Commande c1 = new Commande();
        c1.setNom("cmd1");
        c1.setDispensaire(d1);
        c1.setEnvoyeeLe(LocalDate.of(2023, 1, 1)); // Sent
        commandeRepository.save(c1);

        Categorie cat = new Categorie();
        cat.setLibelle("CatTestItems");
        categorieRepository.save(cat);

        Medicament m1 = new Medicament();
        m1.setNom("MedTestItems");
        m1.setCategorie(cat);
        medicamentRepository.save(m1);

        Ligne l1 = new Ligne();
        l1.setCommande(c1);
        l1.setMedicament(m1);
        l1.setQuantite(5);
        ligneRepository.save(l1);

        Ligne l2 = new Ligne();
        l2.setCommande(c1);
        l2.setMedicament(m1);
        l2.setQuantite(3);
        ligneRepository.save(l2);

        // Another order not sent
        Commande c2 = new Commande();
        c2.setNom("cmd2");
        c2.setDispensaire(d1);
        // envoyeeLe is null (default) or not set
        commandeRepository.save(c2);

        Ligne l3 = new Ligne();
        l3.setCommande(c2);
        l3.setMedicament(m1);
        l3.setQuantite(10);
        ligneRepository.save(l3);

        Long count = commandeRepository.countArticlesCommandesPourDispensaire("DTEST");
        // Only sent orders count: 5 + 3 = 8
        assertEquals(8L, count);
    }

    @Test
    public void testFindCommandesEnCoursPourDispensaire() {
        Dispensaire d1 = new Dispensaire();
        d1.setCode("DENC");
        d1.setNom("DispEnCours");
        d1.setAdresse(new AdressePostale("rue", "12345", "Ville", "Pays", "Region"));
        dispensaireRepository.save(d1);

        Commande c1 = new Commande();
        c1.setNom("cmdSent");
        c1.setDispensaire(d1);
        c1.setEnvoyeeLe(LocalDate.now());
        commandeRepository.save(c1);

        Commande c2 = new Commande();
        c2.setNom("cmdNotSent");
        c2.setDispensaire(d1);
        c2.setEnvoyeeLe(null);
        commandeRepository.save(c2);

        List<Commande> cmds = commandeRepository.findByDispensaireCodeAndEnvoyeeLeIsNull("DENC");
        assertEquals(1, cmds.size());
        assertEquals("cmdNotSent", cmds.get(0).getNom());
    }

    @Test
    public void testFindMedicamentsDisponiblesPourCategorie() {
        Categorie cat = new Categorie();
        cat.setLibelle("CatDispo");
        categorieRepository.save(cat);

        Medicament m1 = new Medicament();
        m1.setNom("Valide");
        m1.setCategorie(cat);
        m1.setIndisponible(false);
        m1.setUnitesEnStock(10);
        m1.setUnitesCommandees(5);
        medicamentRepository.save(m1);

        Medicament m2 = new Medicament();
        m2.setNom("PasAssezDeStock");
        m2.setCategorie(cat);
        m2.setIndisponible(false);
        m2.setUnitesEnStock(5);
        m2.setUnitesCommandees(10);
        medicamentRepository.save(m2);

        Medicament m3 = new Medicament();
        m3.setNom("Indisponible");
        m3.setCategorie(cat);
        m3.setIndisponible(true);
        m3.setUnitesEnStock(20);
        m3.setUnitesCommandees(0);
        medicamentRepository.save(m3);

        List<Medicament> result = medicamentRepository.findMedicamentsDisponiblesPourCategorie(cat.getCode());

        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(m -> m.getNom().equals("Valide")));
    }

}
