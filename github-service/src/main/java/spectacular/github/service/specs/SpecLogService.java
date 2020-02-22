package spectacular.github.service.specs;

import org.springframework.stereotype.Service;
import spectacular.github.service.common.Repository;

@Service
public class SpecLogService {
    private final static String LATEST_AGREED_BRANCH = "master";

    private final SpecService specService;

    public SpecLogService(SpecService specService) {
        this.specService = specService;
    }

    public SpecLog getSpecLogForSpecRepoAndFile(Repository repo, String specFilePath) {
        var latestAgreedSpecItem = specService.getSpecItem(repo, specFilePath, LATEST_AGREED_BRANCH);
        return new SpecLog(latestAgreedSpecItem, null);
    }
}
