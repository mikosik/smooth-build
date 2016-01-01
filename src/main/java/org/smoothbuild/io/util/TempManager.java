package org.smoothbuild.io.util;

import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.io.fs.base.Path.path;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;

@Singleton
public class TempManager {
  private final FileSystem fileSystem;
  private int id = 0;

  @Inject
  public TempManager(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
  }

  public void removeTemps() {
    fileSystem.delete(TEMPORARY_PATH);
  }

  public Path tempPath() {
    id++;
    return TEMPORARY_PATH.append(path(Integer.toString(id)));
  }

  public TempDir tempDir(ValuesDb valuesDb) {
    Path path = tempPath();
    fileSystem.createDir(path);
    return new TempDir(valuesDb, fileSystem, path);
  }
}
