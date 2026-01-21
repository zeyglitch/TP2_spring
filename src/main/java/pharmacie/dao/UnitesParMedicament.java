package pharmacie.dao;

/**
 * Projection pour les résultats d'agrégation de médicaments et quantités.
 * Utilisée pour les requêtes qui retournent le nom d'un médicament et le total d'unités commandées.
 */
public interface UnitesParMedicament {
    String getNom();
    Long getUnites();
}
