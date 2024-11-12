package org.smoothbuild.cli.command.clean;

import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.cli.Artifacts;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.virtualmachine.wire.BytecodeDb;
import org.smoothbuild.virtualmachine.wire.ComputationDb;

public class Clean implements Task0<Tuple0> {
  private final Filesystem filesystem;
  private final FullPath bytecodeDbPath;
  private final FullPath computationDbPath;
  private final FullPath artifactsPath;

  @Inject
  public Clean(
      Filesystem filesystem,
      @BytecodeDb FullPath bytecodeDbPath,
      @ComputationDb FullPath computationDbPath,
      @Artifacts FullPath artifactsPath) {
    this.filesystem = filesystem;
    this.bytecodeDbPath = bytecodeDbPath;
    this.computationDbPath = computationDbPath;
    this.artifactsPath = artifactsPath;
  }

  @Override
  public Output<Tuple0> execute() {
    var logger = new Logger();
    deleteDir("object cache", bytecodeDbPath, logger);
    deleteDir("computation cache", computationDbPath, logger);
    deleteDir("artifacts", artifactsPath, logger);
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
