package spectacular.backend.installation;

import spectacular.backend.github.domain.Installation;

public class InstallationResponse {
  private final int id;
  private final String owner;
  private final String ownerAvatarUrl;

  /**
   * Constructs a new InstallationResponse object.
   *
   * @param id the installation id
   * @param owner the owner name of the installation
   * @param ownerAvatarUrl an avatar image assigned to the owner
   */
  public InstallationResponse(int id, String owner, String ownerAvatarUrl) {
    this.id = id;
    this.owner = owner;
    this.ownerAvatarUrl = ownerAvatarUrl;
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

  public String getOwnerAvatarUrl() {
    return ownerAvatarUrl;
  }
}
