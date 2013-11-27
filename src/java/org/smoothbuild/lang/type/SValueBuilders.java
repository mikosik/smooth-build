package org.smoothbuild.lang.type;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;

public interface SValueBuilders {

  public abstract <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType);

  public abstract FileBuilder fileBuilder();

  public abstract BlobBuilder blobBuilder();

  public abstract SString string(String string);

}
