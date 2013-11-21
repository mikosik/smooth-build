package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.type.Type.FILE;

import java.io.InputStream;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.Blob;
import org.smoothbuild.lang.type.File;

import com.google.common.hash.HashCode;

public class CachedFile extends AbstractValue implements File {
  private final Path path;
  private final CachedBlob content;

  public CachedFile(Path path, CachedBlob content, HashCode hash) {
    super(FILE, hash);
    this.path = checkNotNull(path);
    this.content = checkNotNull(content);
  }

  @Override
  public Path path() {
    return path;
  }

  @Override
  public Blob content() {
    return content;
  }

  @Override
  public InputStream openInputStream() {
    return content.openInputStream();
  }
}
