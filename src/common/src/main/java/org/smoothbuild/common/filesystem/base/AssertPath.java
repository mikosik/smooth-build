package org.smoothbuild.common.filesystem.base;

import java.io.IOException;

public class AssertPath {

  public static void assertPathIsDir(FileSystem<Path> fileSystem, Path path) throws IOException {
    switch (fileSystem.pathState(path)) {
      case DIR -> {}
      case FILE -> throw new IOException("Dir " + path.q() + " doesn't exist. It is a file.");
      case NOTHING -> throw new IOException("Dir " + path.q() + " doesn't exist.");
    }
  }

  public static void assertPathIsFile(FileSystem<Path> fileSystem, Path path) throws IOException {
    switch (fileSystem.pathState(path)) {
      case FILE -> {}
      case DIR -> throw new IOException("File " + path.q() + " doesn't exist. It is a dir.");
      case NOTHING -> throw new IOException("File " + path.q() + " doesn't exist.");
    }
  }

  public static void assertPathExists(FileSystem<Path> fileSystem, Path path) throws IOException {
    switch (fileSystem.pathState(path)) {
      case FILE, DIR -> {}
      case NOTHING -> throw new IOException("Path " + path.q() + " doesn't exist.");
    }
  }

  public static void assertPathIsUnused(FileSystem<Path> fileSystem, Path path) throws IOException {
    switch (fileSystem.pathState(path)) {
      case FILE, DIR -> throw new IOException(
          "Cannot use " + path.q() + " path. It is already taken.");
      case NOTHING -> {}
    }
  }
}
