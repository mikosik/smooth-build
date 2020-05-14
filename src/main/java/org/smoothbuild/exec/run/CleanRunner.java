package org.smoothbuild.exec.run;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.SmoothConstants.HASHED_DB_PATH;
import static org.smoothbuild.SmoothConstants.OUTPUTS_DB_PATH;
import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Console;
import org.smoothbuild.io.fs.base.FileSystem;

public class CleanRunner {
  private final FileSystem fileSystem;
  private final Console console;

  @Inject
  public CleanRunner(FileSystem fileSystem, Console console) {
    this.fileSystem = fileSystem;
    this.console = console;
  }

  public int run() {
    try {
      fileSystem.delete(HASHED_DB_PATH);
      fileSystem.delete(OUTPUTS_DB_PATH);
      fileSystem.delete(ARTIFACTS_PATH);
      fileSystem.delete(TEMPORARY_PATH);
    } catch (IOException e) {
      console.error("Unable to delete " + SMOOTH_DIR + ".");
      return EXIT_CODE_ERROR;
    }
    return EXIT_CODE_SUCCESS;
  }
}
