package org.smoothbuild.io.util;

import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.io.fs.base.Path.path;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.task.exec.Container;

@Singleton
public class TempManager {
  private final FileSystem fileSystem;
  private int id = 0;

  @Inject
  public TempManager(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public Path tempPath() {
    id++;
    return TEMPORARY_PATH.append(path(Integer.toString(id)));
  }

  public TempDir tempDir(Container container) {
    Path path = tempPath();
    fileSystem.createDir(path);
    return new TempDir(container, fileSystem, path);
  }
}
