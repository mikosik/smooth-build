package org.smoothbuild.util.fs.base;

import java.io.IOException;

public class AssertPath {

  public static void assertPathIsDir(FileSystem fileSystem, PathS path) throws IOException {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case DIR -> {}
      case FILE -> throw new IOException("Dir " + path.q() + " doesn't exist. It is a file.");
      case NOTHING -> throw new IOException("Dir " + path.q() + " doesn't exist.");
    }
  }

  public static void assertPathIsFile(FileSystem fileSystem, PathS path) throws IOException {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE -> {}
      case DIR -> throw new IOException("File " + path.q() + " doesn't exist. It is a dir.");
      case NOTHING -> throw new IOException("File " + path.q() + " doesn't exist.");
    }
  }

  public static void assertPathExists(FileSystem fileSystem, PathS path) throws IOException {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE, DIR -> {}
      case NOTHING -> throw new IOException("Path " + path.q() + " doesn't exist.");
    }
  }

  public static void assertPathIsUnused(FileSystem fileSystem, PathS path) throws IOException {
    PathState state = fileSystem.pathState(path);
    switch (state) {
      case FILE, DIR -> throw new IOException("Cannot use " + path + " path. It is already taken.");
      case NOTHING -> {}
    }
  }

  public static RuntimeException newUnknownPathState(PathState state) {
    return new RuntimeException("Unknown " + PathState.class.getName() + ": " + state);
  }
}
