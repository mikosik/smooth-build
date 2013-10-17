package org.smoothbuild.object;

import static org.smoothbuild.fs.base.Path.path;

import java.io.InputStream;

import org.smoothbuild.fs.base.FileSystem;

import com.google.common.hash.HashCode;

public class BlobObject {
  private final FileSystem objectsFileSystem;
  private final HashCode hash;

  public BlobObject(FileSystem objectsFileSystem, HashCode hash) {
    this.objectsFileSystem = objectsFileSystem;
    this.hash = hash;
  }

  public HashCode hash() {
    return hash;
  }

  public InputStream openInputStream() {
    return objectsFileSystem.openInputStream(path(hash.toString()));
  }
}
