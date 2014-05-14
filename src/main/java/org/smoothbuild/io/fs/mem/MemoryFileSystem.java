package org.smoothbuild.io.fs.mem;

import static org.smoothbuild.io.fs.base.AssertPath.assertPathExists;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsDir;
import static org.smoothbuild.io.fs.base.AssertPath.assertPathIsUnused;
import static org.smoothbuild.io.fs.base.PathState.DIR;
import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.smoothbuild.io.fs.base.RecursiveFilesIterable.recursiveFilesIterable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.fs.base.err.FileSystemError;
import org.smoothbuild.io.fs.base.err.IllegalPathForFileError;
import org.smoothbuild.io.fs.base.err.NoSuchDirError;
import org.smoothbuild.io.fs.base.err.NoSuchFileError;

/**
 * In memory implementation of FileSystem.
 */
public class MemoryFileSystem implements FileSystem {
  private final MemoryDirectory root = new MemoryDirectory(null, "");

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
  public List<String> childNames(Path directory) {
    return getDirectory(directory).childNames();
  }

  @Override
  public Iterable<Path> filesFrom(Path directory) {
    assertPathIsDir(this, directory);
    return recursiveFilesIterable(this, directory);
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
    return getFile(path).createInputStream();
  }

  @Override
  public OutputStream openOutputStream(Path path) {
    if (pathState(path) == DIR) {
      throw new IllegalPathForFileError(path);
    }

    MemoryDirectory dir = createDirImpl(path.parent());

    String name = path.lastPart().value();
    if (dir.hasChild(name)) {
      return dir.child(name).createOutputStream();
    } else {
      MemoryFile child = new MemoryFile(dir, name);
      dir.addChild(child);
      return child.createOutputStream();
    }
  }

  @Override
  public void createLink(Path link, Path target) {
    assertPathExists(this, target);
    assertPathIsUnused(this, link);

    String name = link.lastPart().value();
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
      String name = it.next().value();
      if (currentDir.hasChild(name)) {
        MemoryElement child = currentDir.child(name);
        if (child.isDirectory()) {
          currentDir = (MemoryDirectory) child;
        } else {
          throw new FileSystemError("Path (or subpath) of to be created directory (" + directory
              + ") is taken by some file.");
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
      throw new NoSuchFileError(path);
    }
  }

  private MemoryElement getDirectory(Path path) {
    MemoryElement found = findElement(path);
    if (found != null && found.isDirectory()) {
      return found;
    } else {
      throw new NoSuchDirError(path);
    }
  }

  private MemoryElement findElement(Path path) {
    Iterator<Path> it = path.parts().iterator();
    MemoryElement current = root;
    while (it.hasNext()) {
      String name = it.next().value();
      if (current.hasChild(name)) {
        current = current.child(name);
      } else {
        return null;
      }
    }
    return current;
  }
}
