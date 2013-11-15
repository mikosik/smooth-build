package org.smoothbuild.app;

import static org.smoothbuild.io.fs.FileSystemModule.SMOOTH_DIR;

import javax.inject.Inject;

import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;

public class CleanWorker {
  private final FileSystem fileSystem;

  @Inject
  public CleanWorker(@ProjectDir FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public void run() {
    fileSystem.delete(SMOOTH_DIR);
  }
}
