package org.smoothbuild.type.impl;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.api.MutableFileSet;
import org.smoothbuild.type.api.Path;

public class MutableStoredFileSet extends StoredFileSet implements MutableFileSet {

  public MutableStoredFileSet(FileSystem fileSystem) {
    super(fileSystem);
  }

  @Override
  public MutableFile createFile(Path path) {
    return new MutableStoredFile(fileSystem(), path);
  }
}
