package org.smoothbuild.io.fs.mem;

import static org.smoothbuild.io.fs.base.AssertPath.assertPathExists;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsUnused;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.fs.base.err.NoSuchDirException;
import org.smoothbuild.io.fs.base.err.NoSuchFileException;
import org.smoothbuild.io.fs.base.err.PathIsAlreadyTakenByDirException;
import org.smoothbuild.io.fs.base.err.PathIsAlreadyTakenByFileException;

/**
 * In memory implementation of FileSystem.
 */
public class MemoryFileSystem implements FileSystem {
  private final MemoryDirectory root = new MemoryDirectory(null, Path.root());

  @Override
  public PathState pathState(Path path) {
    MemoryElement element = findElement(path);
    if (element == null) {
      return NOTHING;
    }
    if (element.isDirectory()) {
      return DIR;
    }
    return FILE;
  }

  @Override
  public List<Path> filesFrom(Path directory) {
    return getDirectory(directory).childNames();
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
  public InputStream openInputStream(Path path) {
    return getFile(path).openInputStream();
  }

  @Override
  public OutputStream openOutputStream(Path path) {
    if (pathState(path) == DIR) {
      throw new PathIsAlreadyTakenByDirException(path);
    }

    MemoryDirectory dir = createDirImpl(path.parent());

    Path name = path.lastPart();
    if (dir.hasChild(name)) {
      return dir.child(name).openOutputStream();
    } else {
      MemoryFile child = new MemoryFile(dir, name);
      dir.addChild(child);
      return child.openOutputStream();
    }
  }

  @Override
  public void createLink(Path link, Path target) {
    assertPathExists(this, target);
    assertPathIsUnused(this, link);

    Path name = link.lastPart();
    MemoryDirectory dir = createDirImpl(link.parent());
    MemoryElement targetElement = findElement(target);
    dir.addChild(new MemoryLink(dir, name, targetElement));
  }

  @Override
  public void createDir(Path path) {
    createDirImpl(path);
  }

  private MemoryDirectory createDirImpl(Path directory) {
    Iterator<Path> it = directory.parts().iterator();
    MemoryDirectory currentDir = root;
    while (it.hasNext()) {
      Path name = it.next();
      if (currentDir.hasChild(name)) {
        MemoryElement child = currentDir.child(name);
        if (child.isDirectory()) {
          currentDir = (MemoryDirectory) child;
        } else {
          throw new PathIsAlreadyTakenByFileException(directory);
        }
      } else {
        MemoryDirectory newDir = new MemoryDirectory(currentDir, name);
        currentDir.addChild(newDir);
        currentDir = newDir;
      }
    }
    return currentDir;
  }

  private MemoryElement getFile(Path path) {
    MemoryElement found = findElement(path);
    if (found != null && found.isFile()) {
      return found;
    } else {
      throw new NoSuchFileException(path);
    }
  }

  private MemoryElement getDirectory(Path path) {
    MemoryElement found = findElement(path);
    if (found != null && found.isDirectory()) {
      return found;
    } else {
      throw new NoSuchDirException(path);
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
