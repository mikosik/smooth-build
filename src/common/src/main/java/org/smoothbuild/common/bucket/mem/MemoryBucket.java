package org.smoothbuild.common.bucket.mem;

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
import org.smoothbuild.common.bucket.base.AssertPath;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.PathState;

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
    var parent = findElement(path.parent());
    if (parent == null) {
      throw new NoSuchFileException(path.q());
    }
    return switch (resolveLinksFully(parent)) {
      case MemoryFile file -> throw parentExistAsFileException(path);
      case MemoryDir dir -> createSink(dir, path);
      case MemoryLink link -> throw new RuntimeException("Should not happen");
    };
  }

  private static FileSystemException parentExistAsFileException(Path path) {
    return new FileSystemException(
        format("Cannot create sink for {0} because parent path exists and is a file.", path.q()));
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
    MemoryDir dir = createDirImpl(link.parent());
    MemoryElement targetElement = findElement(target);
    dir.addChild(new MemoryLink(dir, name, targetElement));
  }

  @Override
  public void createDir(Path path) throws IOException {
    createDirImpl(path);
  }

  private MemoryDir createDirImpl(Path dir) throws IOException {
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
    return currentDir;
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
