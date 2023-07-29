package org.smoothbuild.run;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.install.ProjectPaths.HASHED_DB_PATH;
import static org.smoothbuild.install.ProjectPaths.SMOOTH_DIR;
import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.util.fs.space.Space.PRJ;

import java.io.IOException;

import org.smoothbuild.out.report.Console;
import org.smoothbuild.util.fs.base.FileSystem;
import org.smoothbuild.util.fs.space.ForSpace;

import jakarta.inject.Inject;

public class CleanRunner {
  private final FileSystem fileSystem;
  private final Console console;

  @Inject
  public CleanRunner(@ForSpace(PRJ) FileSystem fileSystem, Console console) {
    this.fileSystem = fileSystem;
    this.console = console;
  }

  public int run() {
    try {
      fileSystem.delete(HASHED_DB_PATH);
      fileSystem.delete(COMPUTATION_CACHE_PATH);
      fileSystem.delete(ARTIFACTS_PATH);
      fileSystem.delete(TEMPORARY_PATH);
    } catch (IOException e) {
      console.error("Unable to delete " + SMOOTH_DIR + ".");
      return EXIT_CODE_ERROR;
    }
    return EXIT_CODE_SUCCESS;
  }
}
