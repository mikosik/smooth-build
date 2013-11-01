package org.smoothbuild.db.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Value;

import com.google.common.hash.HashCode;

public class FileObject implements File, Value {
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

  @Override
  public HashCode hash() {
    return hash;
  }

  @Override
  public InputStream openInputStream() {
    return content.openInputStream();
  }
}
