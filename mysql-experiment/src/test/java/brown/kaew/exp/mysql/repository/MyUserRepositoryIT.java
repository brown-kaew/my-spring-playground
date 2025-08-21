package brown.kaew.exp.mysql.repository;

import brown.kaew.exp.mysql.model.MyUser;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class MyUserRepositoryIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.41"))
            .withCopyFileToContainer(MountableFile.forClasspathResource("schema-mysql.sql"),
                    "/docker-entrypoint-initdb.d/schema.sql");
    @Autowired
    MyUserRepository myUserRepository;

    @Test
    void findAll_Success() {
        List<MyUser> saved = List.of(
                this.myUserRepository.save(new MyUser(null, "John Doe", "john.doe@mail.com")),
                this.myUserRepository.save(new MyUser(null, "John Doe", "john.doe@mail.com")),
                this.myUserRepository.save(new MyUser(null, "John Doe", "john.doe@mail.com"))
        );

        List<MyUser> all = myUserRepository.findAll();

        Assertions.assertEquals(3, all.size());
        this.myUserRepository.deleteAll(saved);
    }

    @Test
    void findById_Success() {
        MyUser saved = this.myUserRepository.save(new MyUser(null, "John Doe", "john.doe@mail.com"));

        Optional<MyUser> byId = myUserRepository.findById(saved.getId());

        Assertions.assertNotNull(byId);
        Assertions.assertTrue(byId.isPresent());
        MyUser myUser = byId.get();
        Assertions.assertEquals("John Doe", myUser.getName());
        this.myUserRepository.delete(saved);
    }
}