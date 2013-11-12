package org.smoothbuild.app;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;

import javax.inject.Inject;

import org.smoothbuild.fs.base.FileSystem;

public class CleanWorker {
  private final FileSystem fileSystem;

  @Inject
  public CleanWorker(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public void run() {
    fileSystem.delete(BUILD_DIR);
  }
}
