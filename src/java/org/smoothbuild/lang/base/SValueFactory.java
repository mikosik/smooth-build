package org.smoothbuild.lang.base;

import org.smoothbuild.io.fs.base.Path;

public interface SValueFactory {
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType);

  public SFile file(Path path, SBlob content);

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
