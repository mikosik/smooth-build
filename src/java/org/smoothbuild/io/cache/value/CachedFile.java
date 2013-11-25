package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.type.STypes.FILE;

import java.io.InputStream;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;

import com.google.common.hash.HashCode;

public class CachedFile extends AbstractValue implements SFile {
  private final Path path;
  private final SBlob content;

  public CachedFile(Path path, SBlob content, HashCode hash) {
    super(FILE, hash);
    this.path = checkNotNull(path);
    this.content = checkNotNull(content);
  }

  @Override
  public Path path() {
    return path;
  }

  @Override
  public SBlob content() {
    return content;
  }

  @Override
  public InputStream openInputStream() {
    return content.openInputStream();
  }
}
