package org.smoothbuild.lang.value;

import org.smoothbuild.io.fs.base.Path;

public interface ValueFactory {
  public <T extends Value> ArrayBuilder<T> arrayBuilder(Class<T> elementClass);

  public SFile file(Path path, Blob content);

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
