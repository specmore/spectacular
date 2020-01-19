package spectacular.github.service.user;

public class UserResponse {
    private final String login;
    private final String name;
    private final String avatar_url;
    private final String profile_url;

    public UserResponse(String login, String name, String avatar_url, String profile_url) {
        this.login = login;
        this.name = name;
        this.avatar_url = avatar_url;
        this.profile_url = profile_url;
    }

    public String getLogin() {
        return login;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getName() {
        return name;
    }

    public String getProfile_url() {
        return profile_url;
    }
}
