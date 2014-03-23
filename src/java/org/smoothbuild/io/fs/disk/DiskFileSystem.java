package org.smoothbuild.io.fs.disk;

import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.smoothbuild.io.fs.base.RecursiveFilesIterable.recursiveFilesIterable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.fs.base.exc.FileSystemError;
import org.smoothbuild.io.fs.base.exc.NoSuchDirError;
import org.smoothbuild.io.fs.base.exc.NoSuchFileError;
import org.smoothbuild.io.fs.base.exc.NoSuchPathError;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class DiskFileSystem implements FileSystem {
  private final java.nio.file.Path rootDir;

  public DiskFileSystem(String rootDir) {
    this(Paths.get(rootDir));
  }

  public DiskFileSystem(java.nio.file.Path rootDir) {
    this.rootDir = rootDir;
  }

  @Override
  public PathState pathState(Path path) {
    java.nio.file.Path jdkPath = jdkPath(path);
    if (!Files.exists(jdkPath)) {
      return NOTHING;
    }
    if (Files.isDirectory(jdkPath)) {
      return DIR;
    }
    return FILE;
  }

  @Override
  public Iterable<String> childNames(Path directory) {
    assertDirExists(directory);
    try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(jdkPath(directory))) {
      Builder<String> builder = ImmutableList.builder();
      for (java.nio.file.Path path : stream) {
        builder.add(path.getFileName().toString());
      }
      return builder.build();
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }

  @Override
  public Iterable<Path> filesFrom(Path directory) {
    return recursiveFilesIterable(this, directory);
  }

  @Override
  public InputStream openInputStream(Path path) {
    assertFileExists(path);
    try {
      return new BufferedInputStream(Files.newInputStream(jdkPath(path)));
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }

  @Override
  public OutputStream openOutputStream(Path path) {
    if (path.isRoot()) {
      throw new FileSystemError("Cannot open file " + path + " as it is directory.");
    }
    createDir(path.parent());

    try {
      return new BufferedOutputStream(java.nio.file.Files.newOutputStream(jdkPath(path)));
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }

  @Override
  public void createDir(Path path) {
    try {
      Files.createDirectories(jdkPath(path));
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }

  @Override
  public void delete(Path path) {
    if (pathState(path) == NOTHING) {
      return;
    }
    try {
      RecursiveDeleter.deleteRecursively(jdkPath(path));
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }

  @Override
  public void createLink(Path link, Path target) {
    assertPathExists(target);

    if (link.isRoot()) {
      throw new FileSystemError("Cannot create link " + link + " as it is directory.");
    }
    createDir(link.parent());

    try {
      String escape = escapeString(link.parts().size());
      java.nio.file.Path targetJdkPath = Paths.get(escape, target.value());
      Files.createSymbolicLink(jdkPath(link), targetJdkPath);
    } catch (IOException e) {
      throw new FileSystemError(e);
    }
  }

  private static String escapeString(int length) {
    String[] escapeElements = new String[length - 1];
    Arrays.fill(escapeElements, "..");
    return Joiner.on('/').join(escapeElements);
  }

  private void assertDirExists(Path directory) {
    if (pathState(directory) != DIR) {
      throw new NoSuchDirError(directory);
    }
  }

  private void assertFileExists(Path path) {
    if (pathState(path) != FILE) {
      throw new NoSuchFileError(path);
    }
  }

  private void assertPathExists(Path path) {
    if (pathState(path) == NOTHING) {
      throw new NoSuchPathError(path);
    }
  }

  private java.nio.file.Path jdkPath(Path path) {
    if (path.isRoot()) {
      return rootDir;
    } else {
      return rootDir.resolve(path.value());
    }
  }
}
