package org.smoothbuild.common.filesystem.disk;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.filesystem.base.PathState.DIR;
import static org.smoothbuild.common.filesystem.base.PathState.FILE;
import static org.smoothbuild.common.filesystem.base.PathState.NOTHING;
import static org.smoothbuild.common.filesystem.mem.MemoryFileSystem.assertAliasesAreEqual;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.function.Supplier;
import okio.Okio;
import okio.Sink;
import okio.Source;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.common.filesystem.base.Alias;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.PathIterator;
import org.smoothbuild.common.filesystem.base.PathState;
import org.smoothbuild.common.filesystem.base.RecursivePathsIterator;

/**
 * This class is NOT thread-safe.
 */
public class DiskFileSystem implements FileSystem<FullPath> {
  private final Map<Alias, java.nio.file.Path> aliasedDirs;

  public DiskFileSystem(Map<Alias, java.nio.file.Path> aliasedDirs) {
    this.aliasedDirs = aliasedDirs;
  }

  @Override
  public PathState pathState(FullPath path) throws IOException {
    Supplier<String> error = () -> "Cannot check state of " + path.q() + ". ";
    return pathState(jdkPath(path, error), error);
  }

  private static PathState pathState(java.nio.file.Path jdkPath, Supplier<String> error)
      throws IOException {
    var provider = jdkPath.getFileSystem().provider();
    try {
      var attributes = provider.readAttributes(jdkPath, BasicFileAttributes.class);
      return attributes.isDirectory() ? DIR : FILE;
    } catch (NoSuchFileException e) {
      return NOTHING;
    } catch (FileSystemException e) {
      if (e.getMessage().endsWith("Not a directory")) {
        throw noParentDirIOException(error, e);
      } else {
        throw e;
      }
    }
  }

  @Override
  public PathIterator filesRecursively(FullPath dir) {
    return new RecursivePathsIterator(this, dir);
  }

  @Override
  public Iterable<Path> files(FullPath dir) throws IOException {
    Supplier<String> error = () -> "Cannot list files in " + dir.q() + ". ";
    var dirJdk = jdkPath(dir, error);
    assertPathIsDir(dir, dirJdk, error);
    try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(dirJdk)) {
      var builder = new ArrayList<Path>();
      for (java.nio.file.Path path : stream) {
        builder.add(Path.path(path.getFileName().toString()));
      }
      return listOfAll(builder);
    }
  }

  @Override
  public void move(FullPath source, FullPath target) throws IOException {
    // Aliases must be equal to keep symmetry with MemoryFullFileSystem so we can use the same
    // set of tests for both.
    assertAliasesAreEqual(source, target);

    Supplier<String> error = () -> "Cannot move " + source.q() + " to " + target.q() + ". ";
    var sourceJdk = jdkPath(source, error);
    var targetJdk = jdkPath(target, error);
    var sourceState = pathState(sourceJdk, error);
    if (sourceState == NOTHING) {
      throw new IOException(error.get() + "Source doesn't exist.");
    }
    if (sourceState == DIR) {
      throw new IOException(error.get() + "Source is a directory.");
    }
    var targetState = pathState(targetJdk, error);
    if (targetState == DIR) {
      throw new IOException(error.get() + "Target is a directory.");
    }
    Files.move(sourceJdk, targetJdk, ATOMIC_MOVE);
  }

  @Override
  public void deleteRecursively(FullPath path) throws IOException {
    Supplier<String> error = () -> "Cannot delete " + path.q() + ". ";
    var pathJdk = jdkPath(path, error);
    if (pathState(pathJdk, error) == NOTHING) {
      return;
    }
    RecursiveDeleter.deleteRecursively(pathJdk);
  }

  @Override
  public long size(FullPath path) throws IOException {
    Supplier<String> error = () -> "Cannot fetch size of " + path.q() + ". ";
    var pathJdk = jdkPath(path, error);
    assertPathIsFile(path, pathJdk, error);
    return Files.size(pathJdk);
  }

  @Override
  public Source source(FullPath path) throws IOException {
    Supplier<String> error = () -> "Cannot read " + path.q() + ". ";
    var pathJdk = jdkPath(path, error);
    assertPathIsFile(path, pathJdk, error);
    return Okio.source(pathJdk);
  }

  @Override
  public Sink sink(FullPath path) throws IOException {
    Supplier<String> error = () -> "Cannot create sink for " + path.q() + ". ";
    var pathJdk = jdkPath(path, error);
    try {
      if (pathState(pathJdk, error) == DIR) {
        throw new IOException(error.get() + "Cannot use " + path + ". It is already taken by dir.");
      }
    } catch (FileSystemException e) {
      if (e.getMessage().endsWith("Not a directory")) {
        throw noParentDirIOException(error, e);
      } else {
        throw e;
      }
    }
    try {
      return Okio.sink(pathJdk);
    } catch (NoSuchFileException e) {
      throw noParentDirIOException(error, e);
    }
  }

  private static IOException noParentDirIOException(Supplier<String> error, FileSystemException e) {
    return new IOException(error.get() + "Parent dir does not exist.", e);
  }

  @Override
  public void createLink(FullPath link, FullPath target) throws IOException {
    // Aliases must be equal to keep symmetry with MemoryFullFileSystem so we can use the same
    // set of tests for both.
    assertAliasesAreEqual(link, target);

    Supplier<String> error = () -> "Cannot create link " + link.q() + " -> " + target.q() + ". ";
    var targetJdk = jdkPath(target, error);
    var linkJdk = jdkPath(link, error);
    assertPathExists(target, targetJdk, error);
    assertPathIsUnused(link, linkJdk, error);

    var targetRelativeJdk = linkJdk.getParent().relativize(targetJdk);
    try {
      Files.createSymbolicLink(linkJdk, targetRelativeJdk);
    } catch (NoSuchFileException e) {
      throw noParentDirIOException(error, e);
    } catch (UnsupportedOperationException e) {
      // On Filesystems that do not support symbolic link just copy target file.
      Files.copy(linkJdk, targetJdk, REPLACE_EXISTING);
    }
  }

  @Override
  public void createDir(FullPath dir) throws IOException {
    Supplier<String> error = () -> "Cannot create dir " + dir.q() + ". ";
    try {
      Files.createDirectories(jdkPath(dir, error));
    } catch (FileAlreadyExistsException e) {
      throw new IOException(
          error.get() + "Cannot use " + dir.q() + ". It is already taken by file.", e);
    }
  }

  private java.nio.file.Path jdkPath(FullPath path, Supplier<String> error) throws IOException {
    var alias = path.alias();
    var rootDir = aliasedDirs.get(alias);
    if (rootDir == null) {
      throw new IOException(
          error.get() + "Unknown alias " + alias + ". Known aliases = " + aliasedDirs.keySet());
    }
    var shortPath = path.path();
    if (shortPath.isRoot()) {
      return rootDir;
    } else {
      return rootDir.resolve(shortPath.toString());
    }
  }

  private void assertPathExists(FullPath path, java.nio.file.Path pathJdk, Supplier<String> error)
      throws IOException {
    switch (pathState(pathJdk, error)) {
      case FILE, DIR -> {}
      case NOTHING -> throw new IOException(error.get() + "Path " + path.q() + " doesn't exist.");
    }
  }

  private void assertPathIsFile(FullPath path, java.nio.file.Path pathJdk, Supplier<String> error)
      throws IOException {
    switch (pathState(pathJdk, error)) {
      case FILE -> {}
      case DIR -> throw new IOException(
          error.get() + "File " + path.q() + " doesn't exist. It is a dir.");
      case NOTHING -> throw new IOException(error.get() + "File " + path.q() + " doesn't exist.");
    }
  }

  private void assertPathIsDir(FullPath path, java.nio.file.Path pathJdk, Supplier<String> error)
      throws IOException {
    switch (pathState(pathJdk, error)) {
      case DIR -> {}
      case FILE -> throw new IOException(
          error.get() + "Dir " + path.q() + " doesn't exist. It is a file.");
      case NOTHING -> throw new IOException(error.get() + "Dir " + path.q() + " doesn't exist.");
    }
  }

  private void assertPathIsUnused(FullPath path, java.nio.file.Path pathJdk, Supplier<String> error)
      throws IOException {
    switch (pathState(pathJdk, error)) {
      case FILE, DIR -> throw new IOException(
          error.get() + "Cannot use " + path.q() + " path. It is already taken.");
      case NOTHING -> {}
    }
  }
}
