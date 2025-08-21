package brown.kaew.exp.mysql.repository;


import brown.kaew.exp.mysql.model.MyUser;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyUserRepository extends ListCrudRepository<MyUser, Long> {

}
