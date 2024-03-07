package org.smoothbuild.app.run;

import static org.smoothbuild.app.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.app.layout.Layout.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.app.layout.Layout.HASHED_DB_PATH;
import static org.smoothbuild.app.layout.SmoothSpace.PROJECT;
import static org.smoothbuild.common.log.Try.success;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.app.layout.ForSpace;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.log.Logger;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple0;

public class Clean implements TryFunction<Tuple0, String> {
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

  private void deleteDir(Logger logger, Path path) {
    try {
      fileSystem.delete(path);
    } catch (IOException e) {
      logger.error("Unable to delete " + path + ".");
    }
  }
}