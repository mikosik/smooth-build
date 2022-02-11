package org.smoothbuild.fs.disk;

import static java.lang.String.join;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.util.Collections.nCopies;
import static org.smoothbuild.fs.base.AssertPath.assertPathExists;
import static org.smoothbuild.fs.base.AssertPath.assertPathIsDir;
import static org.smoothbuild.fs.base.AssertPath.assertPathIsFile;
import static org.smoothbuild.fs.base.AssertPath.assertPathIsUnused;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.base.PathState.DIR;
import static org.smoothbuild.fs.base.PathState.FILE;
import static org.smoothbuild.fs.base.PathState.NOTHING;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.fs.base.PathState;

import com.google.common.collect.ImmutableList;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;

/**
 * This class is NOT thread-safe.
 */
public class DiskFileSystem implements FileSystem {
  private final Path rootDir;

  public DiskFileSystem(Path path) {
    this.rootDir = path;
  }

  @Override
  public Path rootDirJPath() {
    return rootDir;
  }

  @Override
  public PathState pathState(PathS path) {
    Path jdkPath = jdkPath(path);
    if (!Files.exists(jdkPath)) {
      return NOTHING;
    }
    if (Files.isDirectory(jdkPath)) {
      return DIR;
    }
    return FILE;
  }

  @Override
  public Iterable<PathS> files(PathS dir) throws IOException {
    assertPathIsDir(this, dir);
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(jdkPath(dir))) {
      ImmutableList.Builder<PathS> builder = ImmutableList.builder();
      for (Path path : stream) {
        builder.add(path(path.getFileName().toString()));
      }
      return builder.build();
    }
  }

  @Override
  public void move(PathS source, PathS target) throws IOException {
    if (pathState(source) == NOTHING) {
      throw new IOException("Cannot move " + source.q() + ". It doesn't exist.");
    }
    if (pathState(source) == DIR) {
      throw new IOException("Cannot move " + source.q() + ". It is directory.");
    }
    if (pathState(target) == DIR) {
      throw new IOException("Cannot move to " + target.q() + ". It is directory.");
    }
    PathS targetParent = target.parent();
    if (pathState(targetParent) == NOTHING) {
      createDir(targetParent);
    }
    Files.move(jdkPath(source), jdkPath(target), ATOMIC_MOVE);
  }

  @Override
  public BufferedSource source(PathS path) throws IOException {
    assertPathIsFile(this, path);
    return Okio.buffer(Okio.source(jdkPath(path)));
  }

  @Override
  public BufferedSink sink(PathS path) throws IOException {
    return Okio.buffer(sinkWithoutBuffer(path));
  }

  @Override
  public Sink sinkWithoutBuffer(PathS path) throws IOException {
    if (pathState(path) == DIR) {
      throw new IOException("Cannot use " + path + " path. It is already taken by dir.");
    }
    createDir(path.parent());
    return Okio.sink(jdkPath(path));
  }

  @Override
  public void createDir(PathS path) throws IOException {
    Files.createDirectories(jdkPath(path));
  }

  @Override
  public void delete(PathS path) throws IOException {
    if (pathState(path) == NOTHING) {
      return;
    }
    RecursiveDeleter.deleteRecursively(jdkPath(path));
  }

  @Override
  public void createLink(PathS link, PathS target) throws IOException {
    assertPathExists(this, target);
    assertPathIsUnused(this, link);

    createDir(link.parent());

    String escape = escapeString(link.parts().size() - 1);
    Path targetJdkPath = Path.of(escape, target.toString());
    Files.createSymbolicLink(jdkPath(link), targetJdkPath);
  }

  private static String escapeString(int length) {
    return join("/", nCopies(length, ".."));
  }

  private Path jdkPath(PathS path) {
    if (path.isRoot()) {
      return rootDir;
    } else {
      return rootDir.resolve(path.toString());
    }
  }
}
