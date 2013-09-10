package org.smoothbuild.plugin.internal;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Path;

public class MutableStoredFileSet extends StoredFileSet implements MutableFileSet {

  public MutableStoredFileSet(FileSystem fileSystem) {
    super(fileSystem);
  }

  @Override
  public MutableFile createFile(Path path) {
    return new MutableStoredFile(fileSystem(), path);
  }
}
