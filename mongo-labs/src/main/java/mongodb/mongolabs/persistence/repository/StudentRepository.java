package mongodb.mongolabs.persistence.repository;

import mongodb.mongolabs.persistence.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface StudentRepository extends MongoRepository<Student, String> {
}
