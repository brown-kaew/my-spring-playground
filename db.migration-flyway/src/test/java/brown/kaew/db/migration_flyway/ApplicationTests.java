package brown.kaew.db.migration_flyway;

import brown.kaew.db.migration_flyway.student.StudentRepository;
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
