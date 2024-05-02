package org.smoothbuild.cli.run;

import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.task.Output.output;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.virtualmachine.wire.Project;

public class Clean implements Task0<Void> {
  private final Bucket bucket;

  @Inject
  public Clean(@Project Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public Output<Void> execute() {
    var logger = new Logger();
    deleteDir("object cache", Layout.HASHED_DB_PATH, logger);
    deleteDir("computation cache", Layout.COMPUTATION_CACHE_PATH, logger);
    deleteDir("artifacts", Layout.ARTIFACTS_PATH, logger);
    return output(label("cli", "clean"), logger.toList());
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
