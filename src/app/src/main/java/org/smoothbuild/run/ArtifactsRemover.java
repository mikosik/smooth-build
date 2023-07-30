package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.fs.project.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.fs.project.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.fs.space.Space.PROJECT;
import static org.smoothbuild.out.log.Log.error;

import java.io.IOException;

import org.smoothbuild.common.fs.base.FileSystem;
import org.smoothbuild.common.fs.base.PathS;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.out.report.Reporter;

import jakarta.inject.Inject;

public class ArtifactsRemover {
  private final FileSystem fileSystem;
  private final Reporter reporter;

  @Inject
  public ArtifactsRemover(@ForSpace(PROJECT) FileSystem fileSystem, Reporter reporter) {
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
