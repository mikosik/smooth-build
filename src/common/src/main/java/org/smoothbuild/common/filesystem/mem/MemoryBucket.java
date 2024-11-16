package org.smoothbuild.common.filesystem.mem;

import static java.text.MessageFormat.format;
import static okio.Okio.buffer;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.util.Iterator;
import java.util.List;
import okio.Sink;
import okio.Source;
import org.smoothbuild.common.filesystem.base.AssertPath;
import org.smoothbuild.common.filesystem.base.Bucket;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.PathState;
import org.smoothbuild.common.function.Function1;

/**
 * In memory implementation of Bucket.
 * This class is NOT thread-safe.
 */
public class MemoryBucket implements Bucket {
  private final MemoryDir root = new MemoryDir(null, Path.root());

  public MemoryBucket() {}

  @Override
  public PathState pathState(Path path) {
    MemoryElement elem = findElement(path);
    if (elem == null) {
      return PathState.NOTHING;
    }
    if (elem.isDir()) {
      return PathState.DIR;
    }
    return PathState.FILE;
  }

  @Override
  public List<Path> files(Path dir) throws IOException {
    return getDir(dir).childNames();
  }

  @Override
  public void move(Path source, Path target) throws IOException {
    if (pathState(source) == PathState.NOTHING) {
      throw new IOException("Cannot move " + source.q() + ". It doesn't exist.");
    }
    if (pathState(source) == PathState.DIR) {
      throw new IOException("Cannot move " + source.q() + ". It is directory.");
    }
    if (pathState(target) == PathState.DIR) {
      throw new IOException("Cannot move to " + target.q() + ". It is directory.");
    }
    try (var bufferedSource = buffer(source(source))) {
      try (var sink = sink(target)) {
        bufferedSource.readAll(sink);
      }
    }
    delete(source);
  }

  @Override
  public void delete(Path path) {
    if (path.isRoot()) {
      root.removeAllChildren();
      return;
    }

    MemoryElement elem = findElement(path);
    if (elem == null) {
      return;
    }

    elem.parent().removeChild(elem);
  }

  @Override
  public long size(Path path) throws IOException {
    return getFile(path).size();
  }

  @Override
  public Source source(Path path) throws IOException {
    return getFile(path).source();
  }

  @Override
  public Sink sink(Path path) throws IOException {
    return createObject(path.parent(), (dir) -> createSink(dir, path));
  }

  private <T> T createObject(Path path, Function1<MemoryDir, T, IOException> creator)
      throws IOException {
    var parent = findElement(path);
    if (parent == null) {
      throw new NoSuchFileException(path.q());
    }
    return switch (resolveLinksFully(parent)) {
      case MemoryFile file -> throw parentExistAsFileException(path);
      case MemoryDir dir -> creator.apply(dir);
      case MemoryLink link -> throw new RuntimeException("Should not happen");
    };
  }

  private static FileSystemException parentExistAsFileException(Path path) {
    return new FileSystemException(
        format("Cannot create object because its parent {0} exists and is a file.", path.q()));
  }

  private MemoryElement resolveLinksFully(MemoryElement element) {
    while (element instanceof MemoryLink link) {
      element = link.target();
    }
    return element;
  }

  private static Sink createSink(MemoryDir dir, Path path) throws IOException {
    Path name = path.lastPart();
    if (dir.hasChild(name)) {
      return dir.child(name).sink();
    } else {
      MemoryFile child = new MemoryFile(dir, name);
      dir.addChild(child);
      return child.sink();
    }
  }

  @Override
  public void createLink(Path link, Path target) throws IOException {
    AssertPath.assertPathExists(this, target);
    AssertPath.assertPathIsUnused(this, link);

    Path name = link.lastPart();
    MemoryElement targetElement = findElement(target);
    createObject(link.parent(), (dir) -> {
      dir.addChild(new MemoryLink(dir, name, targetElement));
      return (Void) null;
    });
  }

  @Override
  public void createDir(Path path) throws IOException {
    createDirImpl(path);
  }

  private void createDirImpl(Path dir) throws IOException {
    Iterator<Path> it = dir.parts().iterator();
    MemoryDir currentDir = root;
    while (it.hasNext()) {
      Path name = it.next();
      if (currentDir.hasChild(name)) {
        MemoryElement child = currentDir.child(name);
        if (child.isDir()) {
          currentDir = (MemoryDir) child;
        } else {
          throw new FileAlreadyExistsException(
              "Cannot use " + dir + " path. It is already taken by file.");
        }
      } else {
        MemoryDir newDir = new MemoryDir(currentDir, name);
        currentDir.addChild(newDir);
        currentDir = newDir;
      }
    }
  }

  private MemoryElement getFile(Path path) throws IOException {
    var found = getElement(path);
    if (found.isFile()) {
      return found;
    } else {
      throw new IOException("File " + path.q() + " doesn't exist. It is a dir.");
    }
  }

  private MemoryElement getElement(Path path) throws IOException {
    MemoryElement found = findElement(path);
    if (found == null) {
      throw new IOException("File " + path.q() + " doesn't exist.");
    } else {
      return found;
    }
  }

  private MemoryElement getDir(Path path) throws IOException {
    MemoryElement found = findElement(path);
    if (found == null) {
      throw new IOException("Dir " + path.q() + " doesn't exist.");
    } else {
      if (found.isDir()) {
        return found;
      } else {
        throw new IOException("Dir " + path.q() + " doesn't exist. It is a file.");
      }
    }
  }

  private MemoryElement findElement(Path path) {
    Iterator<Path> it = path.parts().iterator();
    MemoryElement current = root;
    while (it.hasNext()) {
      Path name = it.next();
      if (current.hasChild(name)) {
        current = current.child(name);
      } else {
        return null;
      }
    }
    return current;
  }
}
