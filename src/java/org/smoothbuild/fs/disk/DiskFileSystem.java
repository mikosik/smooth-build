package org.smoothbuild.fs.disk;

import static org.smoothbuild.fs.base.PathState.DIR;
import static org.smoothbuild.fs.base.PathState.FILE;
import static org.smoothbuild.fs.base.PathState.NOTHING;
import static org.smoothbuild.fs.base.RecursiveFilesIterable.recursiveFilesIterable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Paths;

import javax.inject.Singleton;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.PathState;
import org.smoothbuild.fs.base.exc.CannotCreateFileException;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.fs.base.exc.NoSuchDirException;
import org.smoothbuild.fs.base.exc.NoSuchFileException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

@Singleton
public class DiskFileSystem implements FileSystem {
  private final String projectRoot;

  public DiskFileSystem() {
    this(".");
  }

  @VisibleForTesting
  DiskFileSystem(String projectRoot) {
    this.projectRoot = projectRoot;
  }

  @Override
  public Path root() {
    return Path.rootPath();
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
    } catch (NotDirectoryException e) {
      throw new NoSuchDirException(directory);
    } catch (IOException e) {
      throw new FileSystemException(e);
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
    } catch (java.nio.file.NoSuchFileException e) {
      throw new NoSuchFileException(path);
    } catch (IOException e) {
      throw new FileSystemException("Could not read " + path, e);
    }
  }

  @Override
  public OutputStream openOutputStream(Path path) {
    if (path.isRoot()) {
      throw new FileSystemException("Cannot open file " + path + " as it is directory.");
    }
    createDirectory(path.parent());

    try {
      return new BufferedOutputStream(java.nio.file.Files.newOutputStream(jdkPath(path)));
    } catch (IOException e) {
      throw new CannotCreateFileException(path, e);
    }
  }

  private void createDirectory(Path path) {
    try {
      Files.createDirectories(jdkPath(path));
    } catch (FileAlreadyExistsException e) {
      throw new FileSystemException("Could not create directory " + path
          + " as it's either a file or one of its ancestors is a file.");
    } catch (IOException e) {
      throw new FileSystemException("Could not create directory " + path + ".");
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

  private void assertDirExists(Path directory) {
    if (pathState(directory) != DIR) {
      throw new NoSuchDirException(directory);
    }
  }

  private void assertFileExists(Path path) {
    if (pathState(path) != FILE) {
      throw new NoSuchFileException(path);
    }
  }

  private java.nio.file.Path jdkPath(Path path) {
    return Paths.get(projectRoot, path.value());
  }
}
