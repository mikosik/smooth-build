package org.smoothbuild.io.util;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.smoothbuild.SmoothConstants.TEMPORARY_DIR;
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
  private static final Path TEMP_ROOT = SMOOTH_DIR.append(TEMPORARY_DIR);
  private final ValuesDb valuesDb;
  private int id = 0;

  @Inject
  public TempManager(ValuesDb valuesDb) {
    this.valuesDb = valuesDb;
  }

  public void removeTemps() {
    try {
      RecursiveDeleter.deleteRecursively(toJPath(TEMP_ROOT));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Path tempPath() {
    id++;
    return TEMP_ROOT.append(path(Integer.toString(id)));
  }

  public TempDir tempDir() {
    Path path = tempPath();
    java.nio.file.Path jPath = toJPath(path);
    try {
      Files.createDirectories(jPath);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new TempDir(valuesDb, jPath);
  }

  private static java.nio.file.Path toJPath(Path path) {
    return java.nio.file.Paths.get(path.value());
  }
}
