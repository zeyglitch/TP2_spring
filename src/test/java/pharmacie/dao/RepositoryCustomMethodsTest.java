package pharmacie.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pharmacie.entity.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de tests pour vérifier les contraintes d'intégrité et les requêtes métier.
 * 
 * @DataJpaTest : configure automatiquement une base de données H2 en mémoire pour les tests.
 * Les tests sont exécutés dans une transaction qui est automatiquement annulée (rollback) 
 * après chaque test, ce qui garantit l'isolation entre les tests.
 */
@DataJpaTest
public class RepositoryCustomMethodsTest {

    // Injection automatique des repositories (Spring les crée pour nous)
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

    // ========== TESTS DE BASE (CRUD) ==========

    /**
     * Test simple : rechercher des médicaments disponibles
     * Utilise les données du fichier data.sql
     */
    @Test
    public void testRechercheMedicamentsDisponibles() {
        // On récupère des médicaments depuis la base de données
        Medicament indisponible = medicamentRepository.findByNom("Lévofloxacine 500mg").orElseThrow();
        Medicament disponible = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();

        // On cherche tous les médicaments disponibles
        List<Medicament> disponibles = medicamentRepository.findByIndisponibleFalse();

        // Vérifications
        assertTrue(disponibles.contains(disponible), "Le Doliprane devrait être dans la liste");
        assertFalse(disponibles.contains(indisponible), "La Lévofloxacine ne devrait pas être dans la liste");
    }

    /**
     * Test simple : rechercher des catégories par leur nom
     */
    @Test
    public void testRechercheCategories() {
        // On crée deux catégories (avec des noms uniques pour éviter les conflits)
        Categorie analgesiques = new Categorie();
        analgesiques.setLibelle("AnalgesiquesTest2");
        categorieRepository.save(analgesiques);

        Categorie antibiotiques = new Categorie();
        antibiotiques.setLibelle("AntibiotiquesTest2");
        categorieRepository.save(antibiotiques);

        // Test 1 : Recherche exacte par nom
        Categorie trouvee = categorieRepository.findByLibelle("AnalgesiquesTest2");
        assertNotNull(trouvee, "La catégorie devrait être trouvée");
        assertEquals("AnalgesiquesTest2", trouvee.getLibelle());

        // Test 2 : Recherche partielle (contient "Test2")
        List<Categorie> liste = categorieRepository.findByLibelleContaining("Test2");
        assertEquals(2, liste.size(), "On devrait trouver 2 catégories");
    }

    /**
     * Test simple : associer un médicament à une catégorie
     */
    @Test
    public void testAssocierMedicamentCategorie() {
        // On récupère un médicament et on crée une catégorie
        Medicament medicament = medicamentRepository.findByNom("Doliprane Effervescent 1g").orElseThrow();
        
        Categorie categorie = new Categorie();
        categorie.setLibelle("AnalgesiquesTest3");
        categorieRepository.save(categorie);
        
        // On associe le médicament à la catégorie
        medicament.setCategorie(categorie);
        medicamentRepository.save(medicament);
        
        // Vérification
        assertEquals(categorie, medicament.getCategorie(), "Le médicament devrait avoir la bonne catégorie");
    }

    // ========== TESTS DES CONTRAINTES D'INTÉGRITÉ ==========

    /**
     * RÈGLE 1 : Un médicament DOIT avoir une catégorie
     * Ce test vérifie qu'on ne peut pas créer un médicament sans catégorie.
     */
    @Test
    public void testMedicamentSansCategorieDoitEchouer() {
        // On crée un médicament sans lui donner de catégorie
        Medicament medicament = new Medicament();
        medicament.setNom("MedicamentSansCategorie");

        // La sauvegarde doit échouer (lancer une exception)
        assertThrows(Exception.class, () -> {
            medicamentRepository.save(medicament);
            medicamentRepository.flush(); // Force l'écriture en base de données
        });
    }

    /**
     * RÈGLE 2 : On PEUT supprimer une catégorie vide (sans médicaments)
     */
    @Test
    public void testSupprimerCategorieVide() {
        // On crée une catégorie vide
        Categorie categorie = new Categorie();
        categorie.setLibelle("CategorieVide");
        categorieRepository.save(categorie);
        Integer id = categorie.getCode();

        // On la supprime
        categorieRepository.delete(categorie);
        categorieRepository.flush();

        // Elle ne doit plus exister en base
        assertFalse(categorieRepository.findById(id).isPresent(), "La catégorie devrait être supprimée");
    }

    /**
     * RÈGLE 3 : On NE PEUT PAS supprimer une catégorie qui contient des médicaments
     * C'est une contrainte d'intégrité : on ne veut pas avoir des médicaments orphelins.
     */
    @Test
    public void testNePasSupprimerCategorieAvecMedicaments() {
        // On crée une catégorie
        Categorie categorie = new Categorie();
        categorie.setLibelle("CategorieAvecMedicaments");
        categorieRepository.save(categorie);
        Integer id = categorie.getCode();

        // On crée un médicament dans cette catégorie
        Medicament medicament = new Medicament();
        medicament.setNom("Aspirine");
        medicament.setCategorie(categorie);
        medicamentRepository.save(medicament);

        // La suppression doit échouer (lancer une exception)
        assertThrows(Exception.class, () -> {
            categorieRepository.delete(categorie);
            categorieRepository.flush();
        });

        // La catégorie doit toujours exister
        assertTrue(categorieRepository.findById(id).isPresent(), "La catégorie devrait toujours exister");
    }

    /**
     * RÈGLE 4 : Quand on supprime une commande, ses lignes sont automatiquement supprimées
     * C'est ce qu'on appelle une "suppression en cascade".
     */
    @Test
    public void testSuppressionCascadeCommandeVersLignes() {
        // Étape 1 : Créer les données de base (dispensaire, catégorie, médicament)
        Dispensaire dispensaire = creerDispensaire("D1", "Dispensaire Test");
        Medicament medicament = creerMedicament("Paracetamol");

        // Étape 2 : Créer une commande
        Commande commande = new Commande();
        commande.setNom("Commande Test");
        dispensaire.addCommande(commande); // ← Utiliser la méthode utilitaire pour la relation bidirectionnelle
        commande = commandeRepository.save(commande);
        Integer commandeId = commande.getNumero();

        // Étape 3 : Créer des lignes pour cette commande
        Ligne ligne1 = creerLigne(commande, medicament, 10);
        Ligne ligne2 = creerLigne(commande, medicament, 5);
        Long ligne1Id = ligne1.getId();
        Long ligne2Id = ligne2.getId();

        // Étape 4 : Supprimer la commande
        // Important : retirer la commande de la collection du dispensaire d'abord
        dispensaire.removeCommande(commande);
        commandeRepository.delete(commande);
        commandeRepository.flush();

        // Vérifications : la commande ET ses lignes doivent être supprimées
        assertFalse(commandeRepository.findById(commandeId).isPresent(), "La commande devrait être supprimée");
        assertFalse(ligneRepository.findById(ligne1Id).isPresent(), "La ligne 1 devrait être supprimée");
        assertFalse(ligneRepository.findById(ligne2Id).isPresent(), "La ligne 2 devrait être supprimée");
    }

    /**
     * RÈGLE 5 : Quand on supprime un dispensaire, toutes ses commandes (et leurs lignes) sont supprimées
     * C'est une suppression en cascade à deux niveaux : Dispensaire → Commandes → Lignes
     */
    @Test
    public void testSuppressionCascadeDispensaire() {
        // Étape 1 : Créer un dispensaire
        Dispensaire dispensaire = creerDispensaire("D2", "Dispensaire Test");
        Medicament medicament = creerMedicament("Ibuprofene");

        // Étape 2 : Créer deux commandes pour ce dispensaire
        Commande commande1 = new Commande();
        commande1.setNom("Commande 1");
        commande1.setDispensaire(dispensaire);
        commandeRepository.save(commande1);
        Integer cmd1Id = commande1.getNumero();

        Commande commande2 = new Commande();
        commande2.setNom("Commande 2");
        commande2.setDispensaire(dispensaire);
        commandeRepository.save(commande2);
        Integer cmd2Id = commande2.getNumero();

        // Étape 3 : Créer une ligne pour la première commande
        Ligne ligne = creerLigne(commande1, medicament, 15);
        Long ligneId = ligne.getId();

        // Étape 4 : Supprimer le dispensaire
        dispensaireRepository.delete(dispensaire);
        dispensaireRepository.flush();

        // Vérifications : tout doit être supprimé en cascade
        assertFalse(dispensaireRepository.findById("D2").isPresent(), "Le dispensaire devrait être supprimé");
        assertFalse(commandeRepository.findById(cmd1Id).isPresent(), "La commande 1 devrait être supprimée");
        assertFalse(commandeRepository.findById(cmd2Id).isPresent(), "La commande 2 devrait être supprimée");
        assertFalse(ligneRepository.findById(ligneId).isPresent(), "La ligne devrait être supprimée");
    }

    // ========== TESTS DES REQUÊTES MÉTIER ==========

    /**
     * REQUÊTE 1 : Compter le nombre d'articles déjà envoyés pour un dispensaire
     * Important : on ne compte que les commandes déjà envoyées (envoyeeLe != null)
     */
    @Test
    public void testCompterArticlesEnvoyes() {
        // Étape 1 : Créer un dispensaire et un médicament
        Dispensaire dispensaire = creerDispensaire("D3", "Centre Médical");
        Medicament medicament = creerMedicament("Doliprane");

        // Étape 2 : Créer une commande ENVOYÉE avec 2 lignes
        Commande commandeEnvoyee = new Commande();
        commandeEnvoyee.setNom("Commande Envoyée");
        commandeEnvoyee.setDispensaire(dispensaire);
        commandeEnvoyee.setEnvoyeeLe(LocalDate.now()); // ← Commande envoyée !
        commandeRepository.save(commandeEnvoyee);

        creerLigne(commandeEnvoyee, medicament, 10); // 10 articles
        creerLigne(commandeEnvoyee, medicament, 7);  // 7 articles

        // Étape 3 : Créer une commande NON ENVOYÉE
        Commande commandeNonEnvoyee = new Commande();
        commandeNonEnvoyee.setNom("Commande En Cours");
        commandeNonEnvoyee.setDispensaire(dispensaire);
        // envoyeeLe est null → commande pas encore envoyée
        commandeRepository.save(commandeNonEnvoyee);

        creerLigne(commandeNonEnvoyee, medicament, 20); // Ces 20 ne doivent PAS être comptés

        // Étape 4 : Compter les articles envoyés
        Long total = commandeRepository.countArticlesCommandesPourDispensaire("D3");

        // Vérification : seulement 10 + 7 = 17 (la commande non envoyée n'est pas comptée)
        assertEquals(17L, total, "Seuls les articles des commandes envoyées doivent être comptés");
    }

    /**
     * REQUÊTE 2 : Trouver toutes les commandes en cours pour un dispensaire
     * Une commande est "en cours" si elle n'a pas encore été envoyée (envoyeeLe == null)
     */
    @Test
    public void testTrouverCommandesEnCours() {
        // Créer un dispensaire
        Dispensaire dispensaire = creerDispensaire("D4", "Pharmacie Centrale");

        // Créer 1 commande envoyée
        Commande commandeEnvoyee = new Commande();
        commandeEnvoyee.setNom("Commande Déjà Envoyée");
        commandeEnvoyee.setDispensaire(dispensaire);
        commandeEnvoyee.setEnvoyeeLe(LocalDate.now()); // ← Envoyée
        commandeRepository.save(commandeEnvoyee);

        // Créer 2 commandes en cours
        Commande enCours1 = new Commande();
        enCours1.setNom("Commande En Cours 1");
        enCours1.setDispensaire(dispensaire);
        // envoyeeLe est null → en cours
        commandeRepository.save(enCours1);

        Commande enCours2 = new Commande();
        enCours2.setNom("Commande En Cours 2");
        enCours2.setDispensaire(dispensaire);
        // envoyeeLe est null → en cours
        commandeRepository.save(enCours2);

        // Chercher les commandes en cours
        List<Commande> commandesEnCours = commandeRepository.findByDispensaireCodeAndEnvoyeeLeIsNull("D4");

        // Vérifications
        assertEquals(2, commandesEnCours.size(), "Il devrait y avoir 2 commandes en cours");
        assertTrue(commandesEnCours.stream().anyMatch(c -> c.getNom().equals("Commande En Cours 1")));
        assertTrue(commandesEnCours.stream().anyMatch(c -> c.getNom().equals("Commande En Cours 2")));
        assertFalse(commandesEnCours.stream().anyMatch(c -> c.getNom().equals("Commande Déjà Envoyée")));
    }

    /**
     * REQUÊTE 3 : Trouver les médicaments disponibles à la commande
     * Un médicament est disponible si :
     *   1. Il n'est pas marqué comme indisponible
     *   2. Son stock est suffisant (stock >= quantité déjà commandée)
     */
    @Test
    public void testTrouverMedicamentsDisponibles() {
        // Créer une catégorie
        Categorie categorie = new Categorie();
        categorie.setLibelle("Antidouleurs");
        categorieRepository.save(categorie);

        // Médicament 1 : OK (disponible + stock suffisant)
        Medicament med1 = new Medicament();
        med1.setNom("Aspirine");
        med1.setCategorie(categorie);
        med1.setIndisponible(false);    // ← Disponible
        med1.setUnitesEnStock(100);     // ← Beaucoup de stock
        med1.setUnitesCommandees(50);   // ← Moins que le stock → OK
        medicamentRepository.save(med1);

        // Médicament 2 : PAS OK (stock insuffisant)
        Medicament med2 = new Medicament();
        med2.setNom("Paracetamol");
        med2.setCategorie(categorie);
        med2.setIndisponible(false);
        med2.setUnitesEnStock(30);      // ← Pas assez de stock
        med2.setUnitesCommandees(50);   // ← Plus que le stock → PAS OK
        medicamentRepository.save(med2);

        // Médicament 3 : PAS OK (marqué indisponible)
        Medicament med3 = new Medicament();
        med3.setNom("Codeine");
        med3.setCategorie(categorie);
        med3.setIndisponible(true);     // ← Indisponible → PAS OK
        med3.setUnitesEnStock(100);
        med3.setUnitesCommandees(10);
        medicamentRepository.save(med3);

        // Médicament 4 : OK (cas limite : stock = commandées)
        Medicament med4 = new Medicament();
        med4.setNom("Ibuprofene");
        med4.setCategorie(categorie);
        med4.setIndisponible(false);
        med4.setUnitesEnStock(50);      // ← Stock égal
        med4.setUnitesCommandees(50);   // ← Exactement le stock → OK
        medicamentRepository.save(med4);

        // Chercher les médicaments disponibles
        List<Medicament> disponibles = medicamentRepository.findMedicamentsDisponiblesPourCategorie(categorie.getCode());

        // Vérifications : seuls med1 et med4 doivent être disponibles
        assertEquals(2, disponibles.size(), "Il devrait y avoir 2 médicaments disponibles");
        assertTrue(disponibles.stream().anyMatch(m -> m.getNom().equals("Aspirine")));
        assertTrue(disponibles.stream().anyMatch(m -> m.getNom().equals("Ibuprofene")));
    }

    /**
     * REQUÊTE 4 : Agrégation - Total des quantités commandées par médicament
     * Cette requête calcule, pour une catégorie donnée, le total de chaque médicament commandé
     * en additionnant les quantités de toutes les lignes de commande.
     */
    @Test
    public void testAgregerQuantitesCommandees() {
        // Créer une catégorie (nom unique pour éviter les conflits avec data.sql)
        Categorie categorie = new Categorie();
        categorie.setLibelle("AntibiotiquesTest");
        categorieRepository.save(categorie);

        // Créer 2 médicaments
        Medicament med1 = new Medicament();
        med1.setNom("Amoxicilline");
        med1.setCategorie(categorie);
        medicamentRepository.save(med1);

        Medicament med2 = new Medicament();
        med2.setNom("Azithromycine");
        med2.setCategorie(categorie);
        medicamentRepository.save(med2);

        // Créer un dispensaire et 2 commandes
        Dispensaire dispensaire = creerDispensaire("D5", "Hôpital");
        
        Commande commande1 = new Commande();
        commande1.setNom("Commande 1");
        commande1.setDispensaire(dispensaire);
        commandeRepository.save(commande1);

        Commande commande2 = new Commande();
        commande2.setNom("Commande 2");
        commande2.setDispensaire(dispensaire);
        commandeRepository.save(commande2);

        // Créer des lignes de commande
        // med1 est commandé 2 fois : 10 + 15 = 25 au total
        creerLigne(commande1, med1, 10);
        creerLigne(commande2, med1, 15);

        // med2 est commandé 1 fois : 7 au total
        creerLigne(commande1, med2, 7);

        // Exécuter la requête d'agrégation
        List<UnitesParMedicament> resultats = medicamentRepository.medicamentsCommandesPour(categorie.getCode());

        // Vérifications
        assertEquals(2, resultats.size(), "Il devrait y avoir 2 médicaments dans le résultat");

        // Vérifier med1 : total = 25
        UnitesParMedicament resMed1 = resultats.stream()
            .filter(r -> r.getNom().equals("Amoxicilline"))
            .findFirst()
            .orElse(null);
        assertNotNull(resMed1, "Amoxicilline devrait être dans les résultats");
        assertEquals(25L, resMed1.getUnites(), "Total pour Amoxicilline devrait être 25");

        // Vérifier med2 : total = 7
        UnitesParMedicament resMed2 = resultats.stream()
            .filter(r -> r.getNom().equals("Azithromycine"))
            .findFirst()
            .orElse(null);
        assertNotNull(resMed2, "Azithromycine devrait être dans les résultats");
        assertEquals(7L, resMed2.getUnites(), "Total pour Azithromycine devrait être 7");
    }

    // ========== MÉTHODES UTILITAIRES (pour simplifier les tests) ==========

    /**
     * Crée et sauvegarde un dispensaire avec un code et un nom
     */
    private Dispensaire creerDispensaire(String code, String nom) {
        Dispensaire dispensaire = new Dispensaire();
        dispensaire.setCode(code);
        dispensaire.setNom(nom);
        dispensaire.setAdresse(new AdressePostale("rue", "12345", "Ville", "Pays", "Region"));
        return dispensaireRepository.save(dispensaire);
    }

    /**
     * Crée et sauvegarde un médicament avec une catégorie par défaut
     */
    private Medicament creerMedicament(String nom) {
        // Créer une catégorie si elle n'existe pas
        Categorie categorie = categorieRepository.findByLibelle("Medicaments Test");
        if (categorie == null) {
            categorie = new Categorie();
            categorie.setLibelle("Medicaments Test");
            categorieRepository.save(categorie);
        }

        Medicament medicament = new Medicament();
        medicament.setNom(nom);
        medicament.setCategorie(categorie);
        return medicamentRepository.save(medicament);
    }

    /**
     * Crée et sauvegarde une ligne de commande
     */
    private Ligne creerLigne(Commande commande, Medicament medicament, int quantite) {
        Ligne ligne = new Ligne();
        ligne.setCommande(commande);
        ligne.setMedicament(medicament);
        ligne.setQuantite(quantite);
        return ligneRepository.save(ligne);
    }

}
