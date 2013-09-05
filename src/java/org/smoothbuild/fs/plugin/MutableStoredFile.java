package org.smoothbuild.fs.plugin;

import java.io.OutputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.Path;

public class MutableStoredFile extends StoredFile implements MutableFile {
  public MutableStoredFile(FileSystem fileSystem, Path path) {
    super(fileSystem, path);
  }

  @Override
  public OutputStream createOutputStream() {
    return fileSystem().createOutputStream(path());
  }
}
