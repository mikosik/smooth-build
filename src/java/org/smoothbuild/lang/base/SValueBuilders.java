package org.smoothbuild.lang.base;

import org.smoothbuild.db.objects.build.ArrayBuilder;
import org.smoothbuild.db.objects.build.BlobBuilder;
import org.smoothbuild.db.objects.build.FileBuilder;
import org.smoothbuild.db.objects.build.SValueBuildersImpl;

import com.google.inject.ImplementedBy;

@ImplementedBy(SValueBuildersImpl.class)
public interface SValueBuilders {
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType);

  public FileBuilder fileBuilder();

  public BlobBuilder blobBuilder();

  public SString string(String string);
}
