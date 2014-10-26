package org.smoothbuild.lang.base;

import org.smoothbuild.io.fs.base.Path;

public interface SValueFactory {
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(ArrayType<T> arrayType);

  public SFile file(Path path, Blob content);

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
