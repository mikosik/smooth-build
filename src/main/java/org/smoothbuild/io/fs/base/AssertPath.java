package org.smoothbuild.io.fs.base;

import org.smoothbuild.io.fs.base.err.NoSuchDirButFileException;
import org.smoothbuild.io.fs.base.err.NoSuchDirException;
import org.smoothbuild.io.fs.base.err.NoSuchFileButDirException;
import org.smoothbuild.io.fs.base.err.NoSuchFileException;
import org.smoothbuild.io.fs.base.err.NoSuchPathException;
import org.smoothbuild.io.fs.base.err.PathIsAlreadyTakenException;

public class AssertPath {

  public static void assertPathIsDir(FileSystem fileSystem, Path path) {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case DIR:
        return;
      case FILE:
        throw new NoSuchDirButFileException(path);
      case NOTHING:
        throw new NoSuchDirException(path);
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
        throw new NoSuchFileButDirException(path);
      case NOTHING:
        throw new NoSuchFileException(path);
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
        throw new NoSuchPathException(path);
      default:
        throw newUnknownPathState(state);
    }
  }

  public static void assertPathIsUnused(FileSystem fileSystem, Path path) {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE:
        throw new PathIsAlreadyTakenException(path);
      case DIR:
        throw new PathIsAlreadyTakenException(path);
      case NOTHING:
        return;
      default:
        throw newUnknownPathState(state);
    }
  }

  private static RuntimeException newUnknownPathState(PathState state) {
    return new RuntimeException("Unknown " + PathState.class.getName() + ": " + state);
  }
}
