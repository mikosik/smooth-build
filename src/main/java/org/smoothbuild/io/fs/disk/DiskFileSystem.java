package org.smoothbuild.io.fs.disk;

import static java.lang.String.join;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.util.Collections.nCopies;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathExists;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsDir;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsFile;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsUnused;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class DiskFileSystem implements FileSystem {
  private final java.nio.file.Path rootDir;

  public DiskFileSystem(Path path) {
    this.rootDir = path.toJPath();
  }

  DiskFileSystem(java.nio.file.Path path) {
    this.rootDir = path;
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
      Builder<Path> builder = ImmutableList.builder();
      for (java.nio.file.Path path : stream) {
        builder.add(path(path.getFileName().toString()));
      }
      return builder.build();
    }
  }

  @Override
  public void move(Path source, Path target) throws IOException {
    if (pathState(source) == NOTHING) {
      throw new IOException("Cannot move " + source + ". It doesn't exist.");
    }
    if (pathState(source) == DIR) {
      throw new IOException("Cannot move " + source + ". It is directory.");
    }
    if (pathState(target) == DIR) {
      throw new IOException("Cannot move to " + target + ". It is directory.");
    }
    Path targetParent = target.parent();
    if (pathState(targetParent) == NOTHING) {
      createDir(targetParent);
    }
    Files.move(jdkPath(source), jdkPath(target), ATOMIC_MOVE);
  }

  @Override
  public BufferedSource source(Path path) throws IOException {
    assertPathIsFile(this, path);
    return Okio.buffer(Okio.source(jdkPath(path)));
  }

  @Override
  public BufferedSink sink(Path path) throws IOException {
    if (pathState(path) == DIR) {
      throw new IOException("Cannot use " + path + " path. It is already taken by dir.");
    }
    createDir(path.parent());
    return Okio.buffer(Okio.sink(jdkPath(path)));
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

    createDir(link.parent());

    String escape = escapeString(link.parts().size() - 1);
    java.nio.file.Path targetJdkPath = Paths.get(escape, target.value());
    Files.createSymbolicLink(jdkPath(link), targetJdkPath);
  }

  private static String escapeString(int length) {
    return join("/", nCopies(length, ".."));
  }

  private java.nio.file.Path jdkPath(Path path) {
    if (path.isRoot()) {
      return rootDir;
    } else {
      return rootDir.resolve(path.value());
    }
  }
}
