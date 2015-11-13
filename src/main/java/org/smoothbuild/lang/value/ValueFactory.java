package org.smoothbuild.lang.value;

public interface ValueFactory {
  public <T extends Value> ArrayBuilder<T> arrayBuilder(Class<T> elementClass);

  public SFile file(SString path, Blob content);

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
