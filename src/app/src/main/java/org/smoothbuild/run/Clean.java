package org.smoothbuild.run;

import static org.smoothbuild.common.log.Try.success;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.ARTIFACTS_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.HASHED_DB_PATH;
import static org.smoothbuild.filesystem.space.SmoothSpace.PROJECT;

import jakarta.inject.Inject;
import java.io.IOException;
import java.util.function.Function;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.common.log.Logger;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.filesystem.space.ForSpace;

public class Clean implements Function<Tuple0, Try<String>> {
  private final FileSystem fileSystem;

  @Inject
  public Clean(@ForSpace(PROJECT) FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Try<String> apply(Tuple0 argument) {
    var logger = new Logger();
    deleteDir(logger, HASHED_DB_PATH);
    deleteDir(logger, COMPUTATION_CACHE_PATH);
    deleteDir(logger, ARTIFACTS_PATH);
    return success("Cache and artifacts removed.");
  }

  private void deleteDir(Logger logger, PathS path) {
    try {
      fileSystem.delete(path);
    } catch (IOException e) {
      logger.error("Unable to delete " + path + ".");
    }
  }
}
