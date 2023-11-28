package mongodb.mongolabs.persistence.repository;

import mongodb.mongolabs.persistence.entity.Institute;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface InstituteRepository extends MongoRepository<Institute, String> {
    Institute findByName(String name);
}
