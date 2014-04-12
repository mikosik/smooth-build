package org.smoothbuild.lang.base;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.io.cache.value.build.BlobBuilder;
import org.smoothbuild.io.cache.value.build.FileBuilder;
import org.smoothbuild.io.cache.value.build.SValueBuildersImpl;

import com.google.inject.ImplementedBy;

@ImplementedBy(SValueBuildersImpl.class)
public interface SValueBuilders {
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType);

  public FileBuilder fileBuilder();

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
