package org.smoothbuild.install;

import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.FilePath.filePath;
import static org.smoothbuild.fs.space.Space.PRJ;

import java.io.IOException;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.fs.space.FilePath;

import com.google.common.collect.ImmutableList;

public class ProjectPaths {
  public static final PathS SMOOTH_DIR = path(".smooth");
  public static final PathS TEMPORARY_PATH = SMOOTH_DIR.appendPart("temporary");
  public static final PathS ARTIFACTS_PATH = SMOOTH_DIR.appendPart("artifacts");
  public static final PathS COMPUTATION_CACHE_PATH = SMOOTH_DIR.appendPart("computations");
  public static final PathS HASHED_DB_PATH = SMOOTH_DIR.appendPart("hashed");
  public static final PathS SMOOTH_LOCK_PATH = SMOOTH_DIR.appendPart("lock");
  public static final String PRJ_MOD_FILE_NAME = "build.smooth";
  public static final PathS PRJ_MOD_PATH = path(PRJ_MOD_FILE_NAME);
  public static final FilePath PRJ_MOD_FILE_PATH = filePath(PRJ, PRJ_MOD_PATH);

  private static final ImmutableList<PathS> dirsToInitialize =
      ImmutableList.of(HASHED_DB_PATH, COMPUTATION_CACHE_PATH, TEMPORARY_PATH, ARTIFACTS_PATH);

  public static void initializeDirs(FileSystem projectFileSystem) throws IOException {
    for (PathS pathS : dirsToInitialize) {
      initializeDir(projectFileSystem, pathS);
    }
  }

  public static void initializeDir(
      FileSystem fileSystem, PathS dir) throws IOException {
    switch (fileSystem.pathState(dir)) {
      case DIR -> {}
      case FILE -> throw new IOException(
          "Cannot create directory at " + dir.q() + " because it is a file.");
      case NOTHING -> fileSystem.createDir(dir);
    }
  }
}
