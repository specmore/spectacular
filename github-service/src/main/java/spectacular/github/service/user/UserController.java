package spectacular.github.service.user;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("api/user")
    public UserResponse getUser(Authentication authentication) {
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        return createUserFromPrincipal(principal);
    }

    private static UserResponse createUserFromPrincipal(DefaultOAuth2User principal) {
        String login = (String) principal.getAttributes().get("login");
        String name = (String) principal.getAttributes().get("name");
        String avatar_url = (String) principal.getAttributes().get("avatar_url");

        return new UserResponse(login, name, avatar_url);
    }
}
