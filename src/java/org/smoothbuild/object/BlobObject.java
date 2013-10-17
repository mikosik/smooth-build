package org.smoothbuild.object;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.object.HashCodes.toPath;

import java.io.InputStream;

import org.smoothbuild.fs.base.FileSystem;

import com.google.common.hash.HashCode;

public class BlobObject {
  private final FileSystem objectsFileSystem;
  private final HashCode hash;

  public BlobObject(FileSystem objectsFileSystem, HashCode hash) {
    this.objectsFileSystem = checkNotNull(objectsFileSystem);
    this.hash = checkNotNull(hash);
  }

  public HashCode hash() {
    return hash;
  }

  public InputStream openInputStream() {
    return objectsFileSystem.openInputStream(toPath(hash));
  }
}
