package org.smoothbuild.io.fs.base;

import java.io.IOException;

public class AssertPath {

  public static Void assertPathIsDir(FileSystem fileSystem, Path path) throws IOException {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case DIR:
        return null;
      case FILE:
        throw new IOException("Dir " + path + " doesn't exist. It is a file.");
      case NOTHING:
        throw new IOException("Dir " + path + " doesn't exist.");
      default:
        throw newUnknownPathState(state);
    }
  }

  public static Void assertPathIsFile(FileSystem fileSystem, Path path) throws IOException {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE:
        return null;
      case DIR:
        throw new IOException("File " + path + " doesn't exist. It is a dir.");
      case NOTHING:
        throw new IOException("File " + path + " doesn't exist.");
      default:
        throw newUnknownPathState(state);
    }
  }

  public static Void assertPathExists(FileSystem fileSystem, Path path) throws IOException {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE:
        return null;
      case DIR:
        return null;
      case NOTHING:
        throw new IOException("Path " + path + " doesn't exist.");
      default:
        throw newUnknownPathState(state);
    }
  }

  public static Void assertPathIsUnused(FileSystem fileSystem, Path path) throws IOException {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE:
      case DIR:
        throw new IOException("Cannot use " + path + " path. It is already taken.");
      case NOTHING:
        return null;
      default:
        throw newUnknownPathState(state);
    }
  }

  public  static RuntimeException newUnknownPathState(PathState state) {
    return new RuntimeException("Unknown " + PathState.class.getName() + ": " + state);
  }
}
