package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.fs.project.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.fs.project.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.util.collect.Lists.list;

import java.io.IOException;

import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.util.fs.base.FileSystem;
import org.smoothbuild.util.fs.base.PathS;

import jakarta.inject.Inject;

public class ArtifactsRemover {
  private final FileSystem fileSystem;
  private final Reporter reporter;

  @Inject
  public ArtifactsRemover(@ForSpace(PRJ) FileSystem fileSystem, Reporter reporter) {
    this.fileSystem = fileSystem;
    this.reporter = reporter;
  }

  public int removeArtifacts() {
    reporter.startNewPhase("Removing previous artifacts");
    for (PathS path : list(ARTIFACTS_PATH, TEMPORARY_PATH)) {
      try {
        fileSystem.delete(path);
      } catch (IOException e) {
        reporter.report("Deleting " + path, list(error(e.getMessage())));
        return EXIT_CODE_ERROR;
      }
    }
    return EXIT_CODE_SUCCESS;
  }
}
