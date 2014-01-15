package org.smoothbuild.lang.type;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;

public interface SValueBuilders {
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType);

  public FileBuilder fileBuilder();

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
