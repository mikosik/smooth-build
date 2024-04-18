package org.smoothbuild.cli.run;

import static org.smoothbuild.common.log.base.Try.success;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.plan.TryFunction0;
import org.smoothbuild.virtualmachine.wire.Project;

public class Clean implements TryFunction0<Void> {
  private final Bucket bucket;

  @Inject
  public Clean(@Project Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public Label label() {
    return Label.label("cli", "clean");
  }

  @Override
  public Try<Void> apply() {
    var logger = new Logger();
    deleteDir("object cache", Layout.HASHED_DB_PATH, logger);
    deleteDir("computation cache", Layout.COMPUTATION_CACHE_PATH, logger);
    deleteDir("artifacts", Layout.ARTIFACTS_PATH, logger);
    return success(null, logger);
  }

  private void deleteDir(String name, Path path, Logger logger) {
    try {
      bucket.delete(path);
      logger.info(name + " removed");
    } catch (IOException e) {
      logger.error("Unable to delete " + name + " path=" + path.q() + ".");
    }
  }
}
