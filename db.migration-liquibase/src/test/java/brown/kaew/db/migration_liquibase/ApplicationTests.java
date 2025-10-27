package brown.kaew.db.migration_liquibase;

import brown.kaew.db.migration_liquibase.student.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class ApplicationTests {

    @MockitoBean
    StudentRepository studentRepository;

	@Test
	void contextLoads() {
	}

}
