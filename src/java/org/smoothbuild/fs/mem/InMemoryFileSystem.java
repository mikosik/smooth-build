package org.smoothbuild.fs.mem;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.fs.base.PathUtils.WORKING_DIR;
import static org.smoothbuild.fs.base.PathUtils.isValid;
import static org.smoothbuild.fs.base.PathUtils.toCanonical;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.FileSystemException;
import org.smoothbuild.fs.base.PathUtils;
import org.smoothbuild.fs.base.RecursiveFilesIterable;

import com.google.common.io.ByteStreams;

/**
 * In memory implementation of FileSystem. All path arguments are checked for
 * validity and converted to canonical form at the beginning of each method.
 */
public class InMemoryFileSystem implements FileSystem {
  private final InMemoryDirectory root = new InMemoryDirectory("");

  @Override
  public boolean pathExists(String path) {
    return findElement(checked(path)) != null;
  }

  @Override
  public boolean isDirectory(String path) {
    return getElement(checked(path)).isDirectory();
  }

  @Override
  public List<String> childNames(String directory) {
    return getElement(checked(directory)).childNames();
  }

  @Override
  public Iterable<String> filesFrom(String directory) {
    return new RecursiveFilesIterable(this, checked(directory));
  }

  private InMemoryDirectory createDirectory(String directory) {
    Iterator<String> it = PathUtils.toElements(checked(directory)).iterator();
    InMemoryDirectory currentDir = root;
    while (it.hasNext()) {
      String name = it.next();
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
  public void copy(String source, String destination) {
    source = checked(source);
    destination = checked(destination);

    try (InputStream input = createInputStream(source);
        OutputStream output = createOutputStream(destination);) {
      ByteStreams.copy(input, output);
    } catch (IOException e) {
      throw new FileSystemException(
          "Error copying from '" + source + "' to '" + destination + "'.", e);
    }

  }

  @Override
  public InputStream createInputStream(String path) {
    path = checked(path);
    InMemoryElement element = getElement(path);
    if (element.isFile()) {
      return element.createInputStream();
    } else {
      throw new FileSystemException("Cannot read from file '" + path + "' as it is directory.");
    }
  }

  @Override
  public OutputStream createOutputStream(String path) {
    path = checked(path);
    if (path.equals(WORKING_DIR)) {
      throw new FileSystemException("Cannot open file '" + path + "' as it is directory.");
    }
    InMemoryDirectory dir = createDirectory(PathUtils.parentOf(path));

    String fileName = PathUtils.lastElement(path);
    if (dir.hasChild(fileName)) {
      InMemoryElement child = dir.child(fileName);
      if (child.isFile()) {
        return child.createOutputStream();
      } else {
        throw new FileSystemException("Cannot open file '" + path + "' as it is directory.");
      }
    }

    InMemoryFile child = new InMemoryFile(fileName);
    dir.addChild(child);
    return child.createOutputStream();
  }

  private InMemoryElement getElement(String path) {
    InMemoryElement found = findElement(path);
    if (found == null) {
      throw new FileSystemException("Path '" + path + "' doesn't exist");
    } else {
      return found;
    }
  }

  private InMemoryElement findElement(String path) {
    Iterator<String> it = PathUtils.toElements(path).iterator();
    InMemoryElement current = root;
    while (it.hasNext()) {
      String name = it.next();
      if (current.hasChild(name)) {
        current = current.child(name);
      } else {
        return null;
      }
    }
    return current;
  }

  private static String checked(String path) {
    checkArgument(isValid(path));
    return toCanonical(path);
  }
}
