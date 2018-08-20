package org.smoothbuild.io.fs.mem;

import static org.smoothbuild.io.fs.base.AssertPath.assertPathExists;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsUnused;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Sink;

/**
 * In memory implementation of FileSystem.
 */
public class MemoryFileSystem implements FileSystem {
  private final MemoryDir root = new MemoryDir(null, Path.root());

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
  public List<Path> files(Path dir) {
    return getDir(dir).childNames();
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
    try (BufferedSource s = source(source); Sink sink = sink(target)) {
      s.readAll(sink);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
  public BufferedSource source(Path path) {
    return getFile(path).source();
  }

  @Override
  public BufferedSink sink(Path path) {
    if (pathState(path) == DIR) {
      throw new FileSystemException("Cannot use " + path + " path. It is already taken by dir.");
    }

    MemoryDir dir = createDirImpl(path.parent());

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
  public void createLink(Path link, Path target) {
    assertPathExists(this, target);
    assertPathIsUnused(this, link);

    Path name = link.lastPart();
    MemoryDir dir = createDirImpl(link.parent());
    MemoryElement targetElement = findElement(target);
    dir.addChild(new MemoryLink(dir, name, targetElement));
  }

  @Override
  public void createDir(Path path) {
    createDirImpl(path);
  }

  private MemoryDir createDirImpl(Path dir) {
    Iterator<Path> it = dir.parts().iterator();
    MemoryDir currentDir = root;
    while (it.hasNext()) {
      Path name = it.next();
      if (currentDir.hasChild(name)) {
        MemoryElement child = currentDir.child(name);
        if (child.isDir()) {
          currentDir = (MemoryDir) child;
        } else {
          throw new FileSystemException("Cannot use " + dir
              + " path. It is already taken by file.");
        }
      } else {
        MemoryDir newDir = new MemoryDir(currentDir, name);
        currentDir.addChild(newDir);
        currentDir = newDir;
      }
    }
    return currentDir;
  }

  private MemoryElement getFile(Path path) {
    MemoryElement found = findElement(path);
    if (found == null) {
      throw new FileSystemException("File " + path + " doesn't exist.");
    } else {
      if (found.isFile()) {
        return found;
      } else {
        throw new FileSystemException("File " + path + " doesn't exist. It is a dir.");
      }
    }
  }

  private MemoryElement getDir(Path path) {
    MemoryElement found = findElement(path);
    if (found != null && found.isDir()) {
      return found;
    } else {
      throw new FileSystemException("Dir " + path + " doesn't exists.");
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
