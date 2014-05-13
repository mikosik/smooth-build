package org.smoothbuild.io.fs.base;

import org.smoothbuild.io.fs.base.err.NoSuchDirButFileError;
import org.smoothbuild.io.fs.base.err.NoSuchDirError;
import org.smoothbuild.io.fs.base.err.NoSuchFileButDirError;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;
import org.smoothbuild.io.fs.base.err.NoSuchPathError;

public class AssertPath {

  public static void assertPathIsDir(FileSystem fileSystem, Path path) {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case DIR:
        return;
      case FILE:
        throw new NoSuchDirButFileError(path);
      case NOTHING:
        throw new NoSuchDirError(path);
      default:
        throw newUnknownPathState(state);
    }
  }

  public static void assertPathIsFile(FileSystem fileSystem, Path path) {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE:
        return;
      case DIR:
        throw new NoSuchFileButDirError(path);
      case NOTHING:
        throw new NoSuchFileError(path);
      default:
        throw newUnknownPathState(state);
    }
  }

  public static void assertPathExists(FileSystem fileSystem, Path path) {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE:
        return;
      case DIR:
        return;
      case NOTHING:
        throw new NoSuchPathError(path);
      default:
        throw newUnknownPathState(state);
    }
  }

  private static RuntimeException newUnknownPathState(PathState state) {
    return new RuntimeException("Unknown " + PathState.class.getName() + ": " + state);
  }
}
