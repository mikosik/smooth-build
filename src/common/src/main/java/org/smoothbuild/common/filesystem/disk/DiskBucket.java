package org.smoothbuild.common.filesystem.disk;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.filesystem.base.AssertPath.assertPathExists;
import static org.smoothbuild.common.filesystem.base.AssertPath.assertPathIsDir;
import static org.smoothbuild.common.filesystem.base.AssertPath.assertPathIsFile;
import static org.smoothbuild.common.filesystem.base.AssertPath.assertPathIsUnused;
import static org.smoothbuild.common.filesystem.base.PathState.DIR;
import static org.smoothbuild.common.filesystem.base.PathState.FILE;
import static org.smoothbuild.common.filesystem.base.PathState.NOTHING;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import okio.Okio;
import okio.Sink;
import okio.Source;
import org.smoothbuild.common.filesystem.base.Bucket;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.PathState;

/**
 * This class is NOT thread-safe.
 */
public class DiskBucket implements Bucket {
  private final java.nio.file.Path rootDir;

  public DiskBucket(java.nio.file.Path path) {
    this.rootDir = Objects.requireNonNull(path);
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
  public Iterable<Path> files(Path dir) throws IOException {
    assertPathIsDir(this, dir);
    try (DirectoryStream<java.nio.file.Path> stream = Files.newDirectoryStream(jdkPath(dir))) {
      var builder = new ArrayList<Path>();
      for (java.nio.file.Path path : stream) {
        builder.add(Path.path(path.getFileName().toString()));
      }
      return listOfAll(builder);
    }
  }

  @Override
  public void move(Path source, Path target) throws IOException {
    if (pathState(source) == NOTHING) {
      throw new IOException("Cannot move " + source.q() + ". It doesn't exist.");
    }
    if (pathState(source) == DIR) {
      throw new IOException("Cannot move " + source.q() + ". It is directory.");
    }
    if (pathState(target) == DIR) {
      throw new IOException("Cannot move to " + target.q() + ". It is directory.");
    }
    Path targetParent = target.parent();
    if (pathState(targetParent) == NOTHING) {
      createDir(targetParent);
    }
    Files.move(jdkPath(source), jdkPath(target), ATOMIC_MOVE);
  }

  @Override
  public long size(Path path) throws IOException {
    assertPathIsFile(this, path);
    return Files.size(jdkPath(path));
  }

  @Override
  public Source source(Path path) throws IOException {
    assertPathIsFile(this, path);
    return Okio.source(jdkPath(path));
  }

  @Override
  public Sink sink(Path path) throws IOException {
    if (pathState(path) == DIR) {
      throw new IOException("Cannot use " + path + " path. It is already taken by dir.");
    }
    return Okio.sink(jdkPath(path));
  }

  @Override
  public void createDir(Path path) throws IOException {
    Files.createDirectories(jdkPath(path));
  }

  @Override
  public void delete(Path path) throws IOException {
    if (pathState(path) == NOTHING) {
      return;
    }
    RecursiveDeleter.deleteRecursively(jdkPath(path));
  }

  @Override
  public void createLink(Path link, Path target) throws IOException {
    assertPathExists(this, target);
    assertPathIsUnused(this, link);

    var jdkTarget = jdkPath(target);
    var targetJdkPath = jdkPath(link.parent()).relativize(jdkTarget);
    try {
      Files.createSymbolicLink(jdkPath(link), targetJdkPath);
    } catch (UnsupportedOperationException e) {
      // On Filesystems that do not support symbolic link just copy target file.
      Files.copy(jdkPath(link), jdkTarget, REPLACE_EXISTING);
    }
  }

  private java.nio.file.Path jdkPath(Path path) {
    if (path.isRoot()) {
      return rootDir;
    } else {
      return rootDir.resolve(path.toString());
    }
  }
}
