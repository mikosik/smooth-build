package org.smoothbuild.cli.command.clean;

import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.cli.dagger.Artifacts;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task0;
import org.smoothbuild.common.tuple.Tuple0;
import org.smoothbuild.virtualmachine.dagger.BytecodeDb;
import org.smoothbuild.virtualmachine.dagger.ComputationDb;

public class ScheduleClean implements Task0<Tuple0> {
  private final FileSystem<FullPath> fileSystem;
  private final FullPath bytecodeDbPath;
  private final FullPath computationDbPath;
  private final FullPath artifactsPath;

  @Inject
  public ScheduleClean(
      FileSystem<FullPath> fileSystem,
      @BytecodeDb FullPath bytecodeDbPath,
      @ComputationDb FullPath computationDbPath,
      @Artifacts FullPath artifactsPath) {
    this.fileSystem = fileSystem;
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
    return output(tuple(), CleanCommand.LABEL, logger.toList());
  }

  private void deleteDir(String name, FullPath path, Logger logger) {
    try {
      fileSystem.deleteRecursively(path);
      logger.info(name + " removed");
    } catch (IOException e) {
      logger.error("Unable to delete " + name + " path=" + path.q() + ".");
    }
  }
}
