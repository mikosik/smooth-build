package org.smoothbuild.io.fs.mem;

import static org.smoothbuild.io.fs.base.AssertPath.assertPathExists;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsUnused;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.smoothbuild.util.Okios.copyAllAndClose;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;

/**
 * In memory implementation of FileSystem.
 * This class is NOT thread-safe.
 */
public class MemoryFileSystem implements FileSystem {
  private final MemoryDir root = new MemoryDir(null, Path.root());

  @Override
  public java.nio.file.Path rootDirJPath() {
    return java.nio.file.Path.of("in-memory");
  }

  @Override
  public PathState pathState(Path path) {
    MemoryElement element = findElement(path);
    if (element == null) {
      return NOTHING;
    }
    if (element.isDir()) {
      return DIR;
    }
    return FILE;
  }

  @Override
  public List<Path> files(Path dir) throws IOException {
    return getDir(dir).childNames();
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
    copyAllAndClose(source(source), sink(target));
    delete(source);
  }

  @Override
  public void delete(Path path) {
    if (path.isRoot()) {
      root.removeAllChildren();
      return;
    }

    MemoryElement element = findElement(path);
    if (element == null) {
      return;
    }

    element.parent().removeChild(element);
  }

  @Override
  public BufferedSource source(Path path) throws IOException {
    return getFile(path).source();
  }

  @Override
  public BufferedSink sink(Path path) throws IOException {
    return Okio.buffer(sinkWithoutBuffer(path));
  }

  @Override
  public Sink sinkWithoutBuffer(Path path) throws IOException {
    if (pathState(path) == DIR) {
      throw new IOException("Cannot use " + path + " path. It is already taken by dir.");
    }

    MemoryDir dir = createDirImpl(path.parent());

    Path name = path.lastPart();
    if (dir.hasChild(name)) {
      return dir.child(name).sinkWithoutBuffer();
    } else {
      MemoryFile child = new MemoryFile(dir, name);
      dir.addChild(child);
      return child.sinkWithoutBuffer();
    }
  }

  @Override
  public void createLink(Path link, Path target) throws IOException {
    assertPathExists(this, target);
    assertPathIsUnused(this, link);

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
    MemoryElement found = findElement(path);
    if (found == null) {
      throw new IOException("File " + path.q() + " doesn't exist.");
    } else {
      if (found.isFile()) {
        return found;
      } else {
        throw new IOException("File " + path.q() + " doesn't exist. It is a dir.");
      }
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
