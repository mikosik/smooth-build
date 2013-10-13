package org.smoothbuild.run;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;
import static org.smoothbuild.fs.base.PathState.DIR;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;

public class Cleaner {
  private final FileSystem fileSystem;

  @Inject
  public Cleaner(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public void clearBuildDir() {
    if (fileSystem.pathState(BUILD_DIR) == DIR) {
      fileSystem.deleteDirectoryRecursively(BUILD_DIR);
    }
  }
}
