package spectacular.github.service.pullrequests;

import org.springframework.stereotype.Service;
import spectacular.github.service.common.Repository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PullRequestService {
    public List<PullRequest> getPullRequestsForRepo(Repository repo) {
        return new ArrayList<>();
    }
}
