package org.smoothbuild.lang.value;

import org.smoothbuild.lang.type.StructType;
import org.smoothbuild.lang.type.Type;

public interface ValueFactory {
  public ArrayBuilder arrayBuilder(Type elementType);

  public StructBuilder structBuilder(StructType elementType);

  public Struct file(SString path, Blob content);

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
