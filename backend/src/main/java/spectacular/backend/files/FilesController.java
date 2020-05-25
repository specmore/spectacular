package spectacular.backend.files;

import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerMapping;
import spectacular.backend.common.RepositoryId;

@RestController
public class FilesController {
  private static final Logger logger = LoggerFactory.getLogger(FilesController.class);
  private final FilesService filesService;

  public FilesController(FilesService filesService) {
    this.filesService = filesService;
  }

  /**
   * A REST Get operation that wraps the request to the File Service to get the contents of a file stored in a repository
   * that is listed in a catalogue for a given user.
   *
   * @param catalogueOwner the catalogue owner the file is listed in
   * @param catalogueRepoName the repository containing the catalogue the file is listed in
   * @param fileOwner the repository owner the file is stored in
   * @param fileRepoName the repository name the file is stored in
   * @param refName the name of the git commit reference at which to look
   * @param request the request object wrapping this API call
   * @param authToken the Authentication Token associated with the security principle making this API call
   * @return The String contents of the requested file
   * @throws UnsupportedEncodingException if an error occurs when decoding the contents of the file returned by the git source system
   */
  @GetMapping("api/catalogues/{catalogue-owner}/{catalogue-repo}/files/{file-owner}/{file-repo}/{ref-name}/**")
  public ResponseEntity<String> getFileContents(
      @PathVariable("catalogue-owner") String catalogueOwner,
      @PathVariable("catalogue-repo") String catalogueRepoName,
      @PathVariable("file-owner") String fileOwner,
      @PathVariable("file-repo") String fileRepoName,
      @PathVariable("ref-name") String refName,
      HttpServletRequest request,
      JwtAuthenticationToken authToken) throws UnsupportedEncodingException {
    RepositoryId catalogueRepo = new RepositoryId(catalogueOwner, catalogueRepoName);
    RepositoryId fileRepo = new RepositoryId(fileOwner, fileRepoName);
    String path = extractPathFromPattern(request);
    String fileContent;
    try {
      fileContent = filesService.getFileContent(catalogueRepo, fileRepo, path, refName, authToken.getName());
    } catch (HttpClientErrorException.NotFound nf) {
      logger.debug("Failed to retrieve file contents due an file not found on the github api.", nf);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (fileContent == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(fileContent, HttpStatus.OK);
  }

  private static String extractPathFromPattern(final HttpServletRequest request) {
    String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
    String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

    AntPathMatcher apm = new AntPathMatcher();
    String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);

    return finalPath;
  }
}
