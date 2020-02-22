package spectacular.github.service.specs;

import org.springframework.stereotype.Service;
import spectacular.github.service.common.Repository;

@Service
public class SpecEvolutionService {
    private final static String LATEST_AGREED_BRANCH = "master";

    private final SpecService specService;

    public SpecEvolutionService(SpecService specService) {
        this.specService = specService;
    }

    public SpecEvolution getSpecEvolutionForSpecRepoAndFile(Repository repo, String specFilePath) {
        var latestAgreedSpecItem = specService.getSpecItem(repo, specFilePath, LATEST_AGREED_BRANCH);
        return new SpecEvolution(latestAgreedSpecItem, null);
    }
}
