package org.smoothbuild.db.objects;

import javax.inject.Inject;

import org.smoothbuild.db.objects.build.ObjectBuilders;
import org.smoothbuild.db.objects.marshal.ObjectMarshallers;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.base.ValueFactory;

import com.google.common.hash.HashCode;

public class ObjectsDb implements ValueFactory {
  private final ObjectMarshallers objectMarshallers;
  private final ObjectBuilders objectBuilders;

  @Inject
  public ObjectsDb(ObjectMarshallers objectMarshallers, ObjectBuilders objectBuilders) {
    this.objectMarshallers = objectMarshallers;
    this.objectBuilders = objectBuilders;
  }

  @Override
  public <T extends Value> ArrayBuilder<T> arrayBuilder(ArrayType<T> arrayType) {
    return objectBuilders.arrayBuilder(arrayType);
  }

  @Override
  public SFile file(Path path, Blob content) {
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

  public <T extends Value> T read(Type<T> type, HashCode hash) {
    return objectMarshallers.marshaller(type).read(hash);
  }
}
