package org.smoothbuild.run;

import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.ARTIFACTS_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.HASHED_DB_PATH;
import static org.smoothbuild.filesystem.space.Space.PROJECT;
import static org.smoothbuild.out.log.Try.success;

import io.vavr.Tuple0;
import jakarta.inject.Inject;
import java.io.IOException;
import java.util.function.Function;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.filesystem.space.ForSpace;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.out.log.Try;

public class Clean implements Function<Tuple0, Try<String>> {
  private final FileSystem fileSystem;

  @Inject
  public Clean(@ForSpace(PROJECT) FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  @Override
  public Try<String> apply(Tuple0 argument) {
    var logBuffer = new LogBuffer();
    deleteDir(logBuffer, HASHED_DB_PATH);
    deleteDir(logBuffer, COMPUTATION_CACHE_PATH);
    deleteDir(logBuffer, ARTIFACTS_PATH);
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
