package brown.kaew.ttc.service;

import brown.kaew.ttc.model.Profile;
import brown.kaew.ttc.repository.ProfileRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile getProfileById(Long id) {
        return profileRepository.findById(id).orElseThrow();
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }
}
