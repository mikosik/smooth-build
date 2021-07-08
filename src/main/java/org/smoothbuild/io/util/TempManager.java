package org.smoothbuild.io.util;

import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.exec.compute.Container;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.space.ForSpace;

/**
 * This class is thread-safe.
 */
@Singleton
public class TempManager {
  private final FileSystem fileSystem;
  private final AtomicInteger id = new AtomicInteger();

  @Inject
  public TempManager(@ForSpace(PRJ) FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public Path tempPath() {
    return TEMPORARY_PATH.appendPart(Integer.toString(id.getAndIncrement()));
  }

  public TempDir tempDir(Container container) throws IOException {
    Path path = tempPath();
    fileSystem.createDir(path);
    return new TempDir(container, fileSystem, path);
  }
}
