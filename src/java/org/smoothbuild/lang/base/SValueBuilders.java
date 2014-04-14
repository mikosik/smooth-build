package org.smoothbuild.lang.base;


public interface SValueBuilders {
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType);

  public FileBuilder fileBuilder();

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
