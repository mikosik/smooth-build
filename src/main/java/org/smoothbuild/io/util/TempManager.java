package org.smoothbuild.io.util;

import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;
import java.nio.file.Files;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.disk.RecursiveDeleter;

@Singleton
public class TempManager {
  private final ValuesDb valuesDb;
  private int id = 0;

  @Inject
  public TempManager(ValuesDb valuesDb) {
    this.valuesDb = valuesDb;
  }

  public void removeTemps() {
    try {
      RecursiveDeleter.deleteRecursively(TEMPORARY_PATH.toJPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Path tempPath() {
    id++;
    return TEMPORARY_PATH.append(path(Integer.toString(id)));
  }

  public TempDir tempDir() {
    Path path = tempPath();
    try {
      Files.createDirectories(path.toJPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new TempDir(valuesDb, path);
  }
}
