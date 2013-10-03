package org.smoothbuild.type.impl;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.api.MutableFileSet;

public class MutableStoredFileSet extends StoredFileSet implements MutableFileSet {

  public MutableStoredFileSet(FileSystem fileSystem) {
    super(fileSystem);
  }

  @Override
  public MutableFile createFile(Path path) {
    return new MutableStoredFile(fileSystem(), path);
  }
}
