package org.smoothbuild.db.objects.build;

import javax.inject.Inject;

import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.FileBuilder;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.base.SValueBuilders;

public class ObjectBuilders implements SValueBuilders {
  private final ObjectsDb objectsDb;

  @Inject
  public ObjectBuilders(ObjectsDb objectsDb) {
    this.objectsDb = objectsDb;
  }

  @Override
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType) {
    return objectsDb.arrayBuilder(arrayType);
  }

  @Override
  public FileBuilder fileBuilder() {
    return objectsDb.fileBuilder();
  }

  @Override
  public BlobBuilder blobBuilder() {
    return objectsDb.blobBuilder();
  }

  @Override
  public SString string(String string) {
    return objectsDb.string(string);
  }
}
