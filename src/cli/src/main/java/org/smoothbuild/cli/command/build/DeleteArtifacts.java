package org.smoothbuild.cli.command.build;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.task.Output.output;

import jakarta.inject.Inject;
import java.io.IOException;
import org.smoothbuild.cli.Artifacts;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.tuple.Tuple0;

public class DeleteArtifacts implements Task0<Tuple0> {
  private final FileSystem<FullPath> fileSystem;
  private final FullPath artifactsPath;

  @Inject
  public DeleteArtifacts(FileSystem<FullPath> fileSystem, @Artifacts FullPath artifactsPath) {
    this.fileSystem = fileSystem;
    this.artifactsPath = artifactsPath;
  }

  @Override
  public Output<Tuple0> execute() {
    var label = BuildCommand.LABEL.append("deleteArtifacts");
    try {
      fileSystem.deleteRecursively(artifactsPath);
      return output(label, list());
    } catch (IOException e) {
      return output(label, list(error(e.getMessage())));
    }
  }
}
