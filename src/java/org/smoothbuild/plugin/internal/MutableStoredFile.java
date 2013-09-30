package org.smoothbuild.plugin.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.exc.FileSystemException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.MutableFile;
import org.smoothbuild.plugin.api.Path;

import com.google.common.io.ByteStreams;

public class MutableStoredFile extends StoredFile implements MutableFile {
  public MutableStoredFile(FileSystem fileSystem, Path path) {
    super(fileSystem, path);
  }

  @Override
  public OutputStream openOutputStream() {
    return fileSystem().openOutputStream(path());
  }

  @Override
  public void setContent(File file) {
    // TODO when possible fileSystem.copy() should be used
    try (InputStream is = file.openInputStream(); OutputStream os = openOutputStream();) {
      ByteStreams.copy(is, os);
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }
}
