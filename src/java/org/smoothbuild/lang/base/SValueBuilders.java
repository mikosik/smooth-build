package org.smoothbuild.lang.base;

import org.smoothbuild.db.objects.build.ObjectBuilders;

import com.google.inject.ImplementedBy;

@ImplementedBy(ObjectBuilders.class)
public interface SValueBuilders {
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType);

  public FileBuilder fileBuilder();

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
