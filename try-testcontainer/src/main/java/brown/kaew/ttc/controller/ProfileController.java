package brown.kaew.ttc.controller;

import brown.kaew.ttc.model.Profile;
import brown.kaew.ttc.service.ProfileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{id}")
    public Profile getProfile(@PathVariable Long id) {
        return profileService.getProfileById(id);
    }
}
