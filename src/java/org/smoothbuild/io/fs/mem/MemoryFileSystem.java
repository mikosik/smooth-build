package org.smoothbuild.io.fs.mem;

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
import org.smoothbuild.io.fs.base.exc.CannotCreateFileException;
import org.smoothbuild.io.fs.base.exc.FileSystemException;
import org.smoothbuild.io.fs.base.exc.NoSuchDirException;
import org.smoothbuild.io.fs.base.exc.NoSuchFileException;
import org.smoothbuild.io.fs.base.exc.NoSuchPathException;

/**
 * In memory implementation of FileSystem.
 */
public class MemoryFileSystem implements FileSystem {
  private final MemoryDirectory root = new MemoryDirectory(null, "");

  @Override
  public Path root() {
    return Path.rootPath();
  }

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
    return recursiveFilesIterable(this, directory);
  }

  @Override
  public void delete(Path path) {
    MemoryElement element = findElement(path);
    if (element == null) {
      return;
    }
    MemoryDirectory parent = element.parent();
    if (parent == null) {
      throw new IllegalArgumentException("Cannot delete root directory.");
    } else {
      parent.removeChild(element);
    }
  }

  @Override
  public InputStream openInputStream(Path path) {
    return getFile(path).createInputStream();
  }

  @Override
  public OutputStream openOutputStream(Path path) {
    if (path.isRoot()) {
      throw new CannotCreateFileException(path);
    }
    MemoryDirectory dir = createDirectory(path.parent());

    String name = path.lastPart().value();
    if (dir.hasChild(name)) {
      MemoryElement child = dir.child(name);
      if (child.isFile()) {
        return child.createOutputStream();
      } else {
        throw new CannotCreateFileException(path);
      }
    }

    MemoryFile child = new MemoryFile(dir, name);
    dir.addChild(child);
    return child.createOutputStream();
  }

  @Override
  public void createLink(Path link, Path target) {
    MemoryElement targetElement = findElement(target);
    if (targetElement == null) {
      throw new NoSuchPathException(target);
    }

    String name = link.lastPart().value();
    MemoryDirectory dir = createDirectory(link.parent());
    if (dir.hasChild(name)) {
      throw new FileSystemException("Cannot create link as path " + link + " exists.");
    }
    dir.addChild(new MemoryLink(dir, name, targetElement));
  }

  private MemoryDirectory createDirectory(Path directory) {
    Iterator<Path> it = directory.parts().iterator();
    MemoryDirectory currentDir = root;
    while (it.hasNext()) {
      String name = it.next().value();
      if (currentDir.hasChild(name)) {
        MemoryElement child = currentDir.child(name);
        if (child.isDirectory()) {
          currentDir = (MemoryDirectory) child;
        } else {
          throw new FileSystemException("Path (or subpath) of to be created directory ("
              + directory + ") is taken by some file.");
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
