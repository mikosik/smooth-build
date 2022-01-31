package org.smoothbuild.io.fs.mem;

import static org.smoothbuild.io.fs.base.AssertPath.assertPathExists;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsUnused;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.PathS;
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
  private final MemoryDir root = new MemoryDir(null, PathS.root());

  @Override
  public Path rootDirJPath() {
    return Path.of("in-memory");
  }

  @Override
  public PathState pathState(PathS path) {
    MemoryElement elem = findElement(path);
    if (elem == null) {
      return NOTHING;
    }
    if (elem.isDir()) {
      return DIR;
    }
    return FILE;
  }

  @Override
  public List<PathS> files(PathS dir) throws IOException {
    return getDir(dir).childNames();
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
    copyAllAndClose(source(source), sink(target));
    delete(source);
  }

  @Override
  public void delete(PathS path) {
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
  public BufferedSource source(PathS path) throws IOException {
    return getFile(path).source();
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

    MemoryDir dir = createDirImpl(path.parent());

    PathS name = path.lastPart();
    if (dir.hasChild(name)) {
      return dir.child(name).sinkWithoutBuffer();
    } else {
      MemoryFile child = new MemoryFile(dir, name);
      dir.addChild(child);
      return child.sinkWithoutBuffer();
    }
  }

  @Override
  public void createLink(PathS link, PathS target) throws IOException {
    assertPathExists(this, target);
    assertPathIsUnused(this, link);

    PathS name = link.lastPart();
    MemoryDir dir = createDirImpl(link.parent());
    MemoryElement targetElement = findElement(target);
    dir.addChild(new MemoryLink(dir, name, targetElement));
  }

  @Override
  public void createDir(PathS path) throws IOException {
    createDirImpl(path);
  }

  private MemoryDir createDirImpl(PathS dir) throws IOException {
    Iterator<PathS> it = dir.parts().iterator();
    MemoryDir currentDir = root;
    while (it.hasNext()) {
      PathS name = it.next();
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

  private MemoryElement getFile(PathS path) throws IOException {
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

  private MemoryElement getDir(PathS path) throws IOException {
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

  private MemoryElement findElement(PathS path) {
    Iterator<PathS> it = path.parts().iterator();
    MemoryElement current = root;
    while (it.hasNext()) {
      PathS name = it.next();
      if (current.hasChild(name)) {
        current = current.child(name);
      } else {
        return null;
      }
    }
    return current;
  }
}
