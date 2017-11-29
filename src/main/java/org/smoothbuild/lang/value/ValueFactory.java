package org.smoothbuild.lang.value;

import org.smoothbuild.lang.type.Type;

public interface ValueFactory {
  public ArrayBuilder arrayBuilder(Type elementType);

  public Struct file(SString path, Blob content);

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
