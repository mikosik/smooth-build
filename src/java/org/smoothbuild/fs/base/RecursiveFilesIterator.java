package org.smoothbuild.fs.base;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.fs.base.PathUtils.WORKING_DIR;
import static org.smoothbuild.fs.base.PathUtils.append;

import java.util.ArrayDeque;
import java.util.Iterator;

public class RecursiveFilesIterator implements Iterator<String> {
  private final FileSystem fileSystem;
  private final String rootPath;

  private final ArrayDeque<String> directoryStack;
  private final ArrayDeque<String> fileStack;
  private String nextFile;

  public RecursiveFilesIterator(FileSystem fileSystem, String rootPath) {
    checkArgument(fileSystem.isDirectory(rootPath));

    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
    this.directoryStack = new ArrayDeque<String>();
    this.fileStack = new ArrayDeque<String>();
    this.directoryStack.push(WORKING_DIR);

    nextFile = fetchNextFile();
  }

  @Override
  public boolean hasNext() {
    return nextFile != null;
  }

  @Override
  public String next() {
    checkState(hasNext());

    String result = nextFile;
    nextFile = fetchNextFile();
    return result;
  }

  private String fetchNextFile() {
    while (!fileStack.isEmpty() || !directoryStack.isEmpty()) {
      if (fileStack.isEmpty()) {
        String dir = directoryStack.remove();
        String dirFullPath = append(rootPath, dir);
        for (String name : fileSystem.childNames(dirFullPath)) {
          fileStack.add(append(dir, name));
        }
      } else {
        String file = fileStack.remove();
        String fullPath = append(rootPath, file);
        if (fileSystem.isDirectory(fullPath)) {
          directoryStack.add(file);
        } else {
          return file;
        }
      }
    }
    return null;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
