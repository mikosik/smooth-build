package org.smoothbuild.io.cache.value.build;

import javax.inject.Inject;

import org.smoothbuild.io.cache.value.ObjectsDb;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.base.SValueBuilders;

public class SValueBuildersImpl implements SValueBuilders {
  private final ObjectsDb objectsDb;

  @Inject
  public SValueBuildersImpl(ObjectsDb objectsDb) {
    this.objectsDb = objectsDb;
  }

  @Override
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType) {
    return objectsDb.arrayBuilder(arrayType);
  }

  @Override
  public FileBuilder fileBuilder() {
    return new FileBuilder(objectsDb);
  }

  @Override
  public BlobBuilder blobBuilder() {
    return new BlobBuilder(objectsDb);
  }

  @Override
  public SString string(String string) {
    return objectsDb.writeString(string);
  }
}
