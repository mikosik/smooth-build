package org.smoothbuild.fs.mem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.FileSystemException;
import org.smoothbuild.fs.base.RecursiveFilesIterable;
import org.smoothbuild.lang.type.Path;

import com.google.common.io.ByteStreams;

/**
 * In memory implementation of FileSystem.
 */
public class InMemoryFileSystem implements FileSystem {
  private final InMemoryDirectory root = new InMemoryDirectory("");

  @Override
  public boolean pathExists(Path path) {
    return findElement(path) != null;
  }

  @Override
  public boolean isDirectory(Path path) {
    return getElement(path).isDirectory();
  }

  @Override
  public List<String> childNames(Path directory) {
    return getElement(directory).childNames();
  }

  @Override
  public Iterable<Path> filesFrom(Path directory) {
    return new RecursiveFilesIterable(this, directory);
  }

  private InMemoryDirectory createDirectory(Path directory) {
    Iterator<Path> it = directory.toElements().iterator();
    InMemoryDirectory currentDir = root;
    while (it.hasNext()) {
      String name = it.next().value();
      if (currentDir.hasChild(name)) {
        InMemoryElement child = currentDir.child(name);
        if (child.isDirectory()) {
          currentDir = (InMemoryDirectory) child;
        } else {
          throw new FileSystemException(
              "Path (or subpath) of to be created directory is taken by some file.");
        }
      } else {
        InMemoryDirectory newDir = new InMemoryDirectory(name);
        currentDir.addChild(newDir);
        currentDir = newDir;
      }
    }
    return currentDir;
  }

  @Override
  public void copy(Path source, Path destination) {
    try (InputStream input = createInputStream(source);
        OutputStream output = createOutputStream(destination);) {
      ByteStreams.copy(input, output);
    } catch (IOException e) {
      throw new FileSystemException(
          "Error copying from '" + source + "' to '" + destination + "'.", e);
    }

  }

  @Override
  public InputStream createInputStream(Path path) {
    InMemoryElement element = getElement(path);
    if (element.isFile()) {
      return element.createInputStream();
    } else {
      throw new FileSystemException("Cannot read from file '" + path + "' as it is directory.");
    }
  }

  @Override
  public OutputStream createOutputStream(Path path) {
    if (path.isRoot()) {
      throw new FileSystemException("Cannot open file '" + path + "' as it is directory.");
    }
    InMemoryDirectory dir = createDirectory(path.parent());

    String name = path.lastElement().value();
    if (dir.hasChild(name)) {
      InMemoryElement child = dir.child(name);
      if (child.isFile()) {
        return child.createOutputStream();
      } else {
        throw new FileSystemException("Cannot open file '" + path + "' as it is directory.");
      }
    }

    InMemoryFile child = new InMemoryFile(name);
    dir.addChild(child);
    return child.createOutputStream();
  }

  private InMemoryElement getElement(Path path) {
    InMemoryElement found = findElement(path);
    if (found == null) {
      throw new FileSystemException("Path '" + path + "' doesn't exist");
    } else {
      return found;
    }
  }

  private InMemoryElement findElement(Path path) {
    Iterator<Path> it = path.toElements().iterator();
    InMemoryElement current = root;
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
