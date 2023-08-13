package org.smoothbuild.filesystem.project;

import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.filesystem.space.FilePath.filePath;
import static org.smoothbuild.filesystem.space.Space.PROJECT;

import java.io.IOException;

import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.filesystem.space.FilePath;

import com.google.common.collect.ImmutableList;

public class ProjectSpaceLayout {
  public static final PathS SMOOTH_DIR = path(".smooth");
  public static final PathS ARTIFACTS_PATH = SMOOTH_DIR.appendPart("artifacts");
  public static final PathS COMPUTATION_CACHE_PATH = SMOOTH_DIR.appendPart("computations");
  public static final PathS HASHED_DB_PATH = SMOOTH_DIR.appendPart("hashed");
  public static final PathS SMOOTH_LOCK_PATH = SMOOTH_DIR.appendPart("lock");
  public static final String DEFAULT_MODULE_FILE_NAME = "build.smooth";
  public static final PathS DEFAULT_MODULE_PATH = path(DEFAULT_MODULE_FILE_NAME);
  public static final FilePath DEFAULT_MODULE_FILE_PATH = filePath(PROJECT, DEFAULT_MODULE_PATH);

  private static final ImmutableList<PathS> dirsToInitialize =
      ImmutableList.of(HASHED_DB_PATH, COMPUTATION_CACHE_PATH, ARTIFACTS_PATH);

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