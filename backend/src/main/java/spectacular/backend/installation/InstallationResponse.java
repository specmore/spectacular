package spectacular.backend.installation;

import spectacular.backend.github.app.Installation;

public class InstallationResponse {
    private final int id;
    private final String owner;
    private final String owner_avatar_url;

    public InstallationResponse(int id, String owner, String owner_avatar_url) {
        this.id = id;
        this.owner = owner;
        this.owner_avatar_url = owner_avatar_url;
    }

    public InstallationResponse(Installation installation) {
        this(installation.getId(), installation.getAccount().getLogin(), installation.getAccount().getAvatar_url());
    }

    public int getId() {
        return id;
    }

    public String getOwner() {
        return owner;
    }

    public String getOwner_avatar_url() {
        return owner_avatar_url;
    }
}
