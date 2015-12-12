package org.smoothbuild.io.fs.base;

public class AssertPath {

  public static Void assertPathIsDir(FileSystem fileSystem, Path path) {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case DIR:
        return null;
      case FILE:
        throw new FileSystemException("Dir " + path + " doesn't exist. It is a file.");
      case NOTHING:
        throw new FileSystemException("Dir " + path + " doesn't exists.");
      default:
        throw newUnknownPathState(state);
    }
  }

  public static Void assertPathIsFile(FileSystem fileSystem, Path path) {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE:
        return null;
      case DIR:
        throw new FileSystemException("File " + path + " doesn't exist. It is a dir.");
      case NOTHING:
        throw new FileSystemException("File " + path + " doesn't exist.");
      default:
        throw newUnknownPathState(state);
    }
  }

  public static Void assertPathExists(FileSystem fileSystem, Path path) {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE:
        return null;
      case DIR:
        return null;
      case NOTHING:
        throw new FileSystemException("Path " + path + " doesn't exists.");
      default:
        throw newUnknownPathState(state);
    }
  }

  public static Void assertPathIsUnused(FileSystem fileSystem, Path path) {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE:
      case DIR:
        throw new FileSystemException("Cannot use " + path + " path. It is already taken.");
      case NOTHING:
        return null;
      default:
        throw newUnknownPathState(state);
    }
  }

  private static RuntimeException newUnknownPathState(PathState state) {
    return new RuntimeException("Unknown " + PathState.class.getName() + ": " + state);
  }
}
