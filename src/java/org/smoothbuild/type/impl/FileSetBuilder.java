package org.smoothbuild.type.impl;

import java.io.OutputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.FileSet;

public class FileSetBuilder {
  private final MutableStoredFileSet fileSet;

  public FileSetBuilder(FileSystem fileSystem) {
    this.fileSet = new MutableStoredFileSet(fileSystem);
  }

  public void add(File file) {
    fileSet.createFile(file.path()).setContent(file);
  }

  public OutputStream openFileOutputStream(Path path) {
    // TODO each opened OutputStrea should add "path->null" entry to some map.
    // Closing OutputStream should assert that entry still maps to null and
    // change mapping to File.hash().
    return fileSet.openFileOutputStream(path);
  }

  public boolean contains(Path path) {
    return fileSet.contains(path);
  }

  public FileSet build() {
    return fileSet;
  }
}
