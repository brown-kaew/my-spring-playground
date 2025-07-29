package brown.kaew.ttc.service;

import static org.junit.jupiter.api.Assertions.*;

import brown.kaew.ttc.model.Profile;
import brown.kaew.ttc.repository.ProfileRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class ProfileServiceIT {

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>(DockerImageName.parse("mysql:5.7.34"));

    @Autowired
    ProfileService profileService;

    @Autowired
    ProfileRepository profileRepository;

    @Test
    void testGetAll() {
        List<Profile> saved = List.of(
                this.profileRepository.save(new Profile(null, "John Doe", "john.doe@mail.com")),
                this.profileRepository.save(new Profile(null, "John Doe", "john.doe@mail.com")),
                this.profileRepository.save(new Profile(null, "John Doe", "john.doe@mail.com"))
        );

        List<Profile> profiles = profileService.getAllProfiles();

        assertEquals(3, profiles.size());
        this.profileRepository.deleteAll(saved);
    }

    @Test
    void testGetProfileById() {
        Profile saved = this.profileRepository.save(new Profile(null, "John Doe", "john.doe@mail.com"));

        Profile profile = profileService.getProfileById(saved.getId());

        assertNotNull(profile);
        assertEquals("John Doe", profile.getName());
        this.profileRepository.delete(saved);
    }

}