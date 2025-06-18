package brown.kaew.ttc.repository;

import brown.kaew.ttc.model.Profile;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends ListCrudRepository<Profile, Long> {

}
