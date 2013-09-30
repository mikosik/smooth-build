package org.smoothbuild.fs.mem;

import static org.smoothbuild.fs.base.PathKind.DIR;
import static org.smoothbuild.fs.base.PathKind.FILE;
import static org.smoothbuild.fs.base.PathKind.NOTHING;
import static org.smoothbuild.fs.base.RecursiveFilesIterable.recursiveFilesIterable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.PathKind;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.fs.base.exc.NoSuchDirException;
import org.smoothbuild.fs.base.exc.NoSuchFileException;
import org.smoothbuild.plugin.api.Path;

import com.google.common.io.ByteStreams;

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
  public PathKind pathKind(Path path) {
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

  private MemoryDirectory createDirectory(Path directory) {
    Iterator<Path> it = directory.toElements().iterator();
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

  @Override
  public void copy(Path source, Path destination) {
    try (InputStream input = openInputStream(source);
        OutputStream output = openOutputStream(destination);) {
      ByteStreams.copy(input, output);
    } catch (IOException e) {
      throw new FileSystemException(
          "Error copying from '" + source + "' to '" + destination + "'.", e);
    }

  }

  @Override
  public void deleteDirectoryRecursively(Path directory) {
    MemoryElement element = getDirectory(directory);
    if (element.isDirectory()) {
      MemoryDirectory parent = element.parent();
      if (parent == null) {
        throw new IllegalArgumentException("Cannot delete root directory.");
      } else {
        parent.removeChild(element);
      }
    } else {
      throw new NoSuchDirException(directory);
    }
  }

  @Override
  public InputStream openInputStream(Path path) {
    MemoryElement element = getFile(path);
    if (element.isFile()) {
      return element.createInputStream();
    } else {
      throw new FileSystemException("Cannot read from file '" + path + "' as it is directory.");
    }
  }

  @Override
  public OutputStream openOutputStream(Path path) {
    if (path.isRoot()) {
      throw new FileSystemException("Cannot open file '" + path + "' as it is directory.");
    }
    MemoryDirectory dir = createDirectory(path.parent());

    String name = path.lastElement().value();
    if (dir.hasChild(name)) {
      MemoryElement child = dir.child(name);
      if (child.isFile()) {
        return child.createOutputStream();
      } else {
        throw new FileSystemException("Cannot open file '" + path + "' as it is directory.");
      }
    }

    MemoryFile child = new MemoryFile(dir, name);
    dir.addChild(child);
    return child.createOutputStream();
  }

  private MemoryElement getFile(Path path) {
    MemoryElement found = findElement(path);
    if (found == null) {
      throw new NoSuchFileException(path);
    } else {
      return found;
    }
  }

  private MemoryElement getDirectory(Path path) {
    MemoryElement found = findElement(path);
    if (found == null) {
      throw new NoSuchDirException(path);
    } else {
      return found;
    }
  }

  private MemoryElement findElement(Path path) {
    Iterator<Path> it = path.toElements().iterator();
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
