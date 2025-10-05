package brown.kaew.db.migration_liquibase.student;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends ListCrudRepository<Student, Long> {

}
