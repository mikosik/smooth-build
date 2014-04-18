package org.smoothbuild.db.objects;

import javax.inject.Inject;

import org.smoothbuild.db.objects.build.ObjectBuilders;
import org.smoothbuild.db.objects.marshal.ObjectMarshallers;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.base.SValueBuilders;

import com.google.common.hash.HashCode;

public class ObjectsDb implements SValueBuilders {
  private final ObjectMarshallers objectMarshallers;
  private final ObjectBuilders objectBuilders;

  @Inject
  public ObjectsDb(ObjectMarshallers objectMarshallers, ObjectBuilders objectBuilders) {
    this.objectMarshallers = objectMarshallers;
    this.objectBuilders = objectBuilders;
  }

  @Override
  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType) {
    return objectBuilders.arrayBuilder(arrayType);
  }

  @Override
  public SFile file(Path path, SBlob content) {
    return objectMarshallers.fileMarshaller().write(path, content);
  }

  @Override
  public BlobBuilder blobBuilder() {
    return objectBuilders.blobBuilder();
  }

  @Override
  public SString string(String string) {
    return objectMarshallers.stringMarshaller().write(string);
  }

  public <T extends SValue> T read(SType<T> type, HashCode hash) {
    return objectMarshallers.marshaller(type).read(hash);
  }
}
