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

    public Profile createProfile(Profile profile) {
        profile.setId(null); // Ensure the ID is null for new profiles
        return profileRepository.save(profile);
    }

    public Profile updateProfile(Long id, Profile profile) {
        if (!profileRepository.existsById(id)) {
            throw new IllegalArgumentException("Profile not found");
        }
        profile.setId(id); // Set the ID for the existing profile
        return profileRepository.save(profile);
    }

    public void deleteProfile(Long id) {
        if (!profileRepository.existsById(id)) {
            throw new IllegalArgumentException("Profile not found");
        }
        profileRepository.deleteById(id);
    }

}
