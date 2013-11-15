package org.smoothbuild.io.cache.value;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.function.base.Type;
import org.smoothbuild.lang.function.value.Blob;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.lang.function.value.Value;

import com.google.common.hash.HashCode;

public class CachedFile implements File, Value {
  private final Path path;
  private final CachedBlob content;
  private final HashCode hash;

  public CachedFile(Path path, CachedBlob content, HashCode hash) {
    this.path = checkNotNull(path);
    this.content = checkNotNull(content);
    this.hash = checkNotNull(hash);
  }

  @Override
  public Type type() {
    return Type.FILE;
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
  public HashCode hash() {
    return hash;
  }

  @Override
  public InputStream openInputStream() {
    return content.openInputStream();
  }
}
