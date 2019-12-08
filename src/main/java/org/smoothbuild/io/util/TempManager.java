package org.smoothbuild.io.util;

import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.exec.task.Container;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;

/**
 * This class is thread safe.
 */
@Singleton
public class TempManager {
  private final FileSystem fileSystem;
  private AtomicInteger id = new AtomicInteger();

  @Inject
  public TempManager(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public Path tempPath() {
    return TEMPORARY_PATH.append(path(Integer.toString(id.getAndIncrement())));
  }

  public TempDir tempDir(Container container) throws IOException {
    Path path = tempPath();
    fileSystem.createDir(path);
    return new TempDir(container, fileSystem, path);
  }
}
