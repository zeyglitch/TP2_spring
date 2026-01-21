package pharmacie.dao;

import org.springframework.data.repository.CrudRepository;
import pharmacie.entity.Ligne;

public interface LigneRepository extends CrudRepository<Ligne, Long> {
}
