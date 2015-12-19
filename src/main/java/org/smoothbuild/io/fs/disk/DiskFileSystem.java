package org.smoothbuild.io.fs.disk;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.joining;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathExists;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsDir;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsFile;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsUnused;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;

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
  public Iterable<Path> files(Path dir) {
    assertPathIsDir(this, dir);
    try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(jdkPath(
        dir))) {
      Builder<Path> builder = ImmutableList.builder();
      for (java.nio.file.Path path : stream) {
        builder.add(path(path.getFileName().toString()));
      }
      return builder.build();
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  @Override
  public void move(Path source, Path target) {
    if (pathState(source) == NOTHING) {
      throw new FileSystemException("Cannot move " + source + ". It doesn't exist.");
    }
    if (pathState(source) == DIR) {
      throw new FileSystemException("Cannot move " + source + ". It is directory.");
    }
    if (pathState(target) == DIR) {
      throw new FileSystemException("Cannot move to " + target + ". It is directory.");
    }
    try {
      Files.move(jdkPath(source), jdkPath(target), ATOMIC_MOVE);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  @Override
  public InputStream openInputStream(Path path) {
    assertPathIsFile(this, path);
    try {
      return new BufferedInputStream(Files.newInputStream(jdkPath(path)));
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  @Override
  public OutputStream openOutputStream(Path path) {
    if (pathState(path) == DIR) {
      throw new FileSystemException("Cannot use " + path + " path. It is already taken by dir.");
    }

    createDir(path.parent());

    try {
      return new BufferedOutputStream(java.nio.file.Files.newOutputStream(jdkPath(path)));
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  @Override
  public void createDir(Path path) {
    try {
      Files.createDirectories(jdkPath(path));
    } catch (FileAlreadyExistsException e) {
      throw new FileSystemException("Cannot use " + path + " path. It is already taken by file.");
    } catch (IOException e) {
      throw new FileSystemException(e);
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
      throw new FileSystemException(e);
    }
  }

  @Override
  public void createLink(Path link, Path target) {
    assertPathExists(this, target);
    assertPathIsUnused(this, link);

    createDir(link.parent());

    try {
      String escape = escapeString(link.parts().size() - 1);
      java.nio.file.Path targetJdkPath = Paths.get(escape, target.value());
      Files.createSymbolicLink(jdkPath(link), targetJdkPath);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private static String escapeString(int length) {
    return nCopies(length, "..").stream().collect(joining("/"));
  }

  private java.nio.file.Path jdkPath(Path path) {
    if (path.isRoot()) {
      return rootDir;
    } else {
      return rootDir.resolve(path.value());
    }
  }
}
