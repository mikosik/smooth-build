package org.smoothbuild.lang.base;

import org.smoothbuild.io.fs.base.Path;

public interface ValueFactory {
  public <T extends Value> ArrayBuilder<T> arrayBuilder(ArrayType<T> arrayType);

  public SFile file(Path path, Blob content);

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
