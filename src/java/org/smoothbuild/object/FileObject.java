package org.smoothbuild.object;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.type.api.File;

import com.google.common.hash.HashCode;

public class FileObject implements File {
  private final Path path;
  private final BlobObject content;
  private final HashCode hash;

  public FileObject(Path path, BlobObject content, HashCode hash) {
    this.path = checkNotNull(path);
    this.content = checkNotNull(content);
    this.hash = checkNotNull(hash);
  }

  @Override
  public Path path() {
    return path;
  }

  public HashCode hash() {
    return hash;
  }

  @Override
  public InputStream openInputStream() {
    return content.openInputStream();
  }
}
