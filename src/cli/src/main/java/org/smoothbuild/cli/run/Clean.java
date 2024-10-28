package org.smoothbuild.cli.run;

import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.cli.layout.Layout;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.tuple.Tuple0;

public class Clean implements Task0<Tuple0> {
  private final Filesystem filesystem;

  @Inject
  public Clean(Filesystem filesystem) {
    this.filesystem = filesystem;
  }

  @Override
  public Output<Tuple0> execute() {
    var logger = new Logger();
    deleteDir("object cache", Layout.BYTECODE_DB, logger);
    deleteDir("computation cache", Layout.COMPUTATION_DB, logger);
    deleteDir("artifacts", Layout.ARTIFACTS, logger);
    return output(tuple(), label("cli", "clean"), logger.toList());
  }

  private void deleteDir(String name, FullPath path, Logger logger) {
    try {
      filesystem.delete(path);
      logger.info(name + " removed");
    } catch (IOException e) {
      logger.error("Unable to delete " + name + " path=" + path.q() + ".");
    }
  }
}
