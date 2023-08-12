package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.ARTIFACTS_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.HASHED_DB_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.SMOOTH_DIR;
import static org.smoothbuild.filesystem.space.Space.PROJECT;

import java.io.IOException;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.filesystem.space.ForSpace;
import org.smoothbuild.out.report.Console;

import jakarta.inject.Inject;

public class CleanRunner {
  private final FileSystem fileSystem;
  private final Console console;

  @Inject
  public CleanRunner(@ForSpace(PROJECT) FileSystem fileSystem, Console console) {
    this.fileSystem = fileSystem;
    this.console = console;
  }

  public int run() {
    try {
      fileSystem.delete(HASHED_DB_PATH);
      fileSystem.delete(COMPUTATION_CACHE_PATH);
      fileSystem.delete(ARTIFACTS_PATH);
    } catch (IOException e) {
      console.error("Unable to delete " + SMOOTH_DIR + ".");
      return EXIT_CODE_ERROR;
    }
    return EXIT_CODE_SUCCESS;
  }
}
