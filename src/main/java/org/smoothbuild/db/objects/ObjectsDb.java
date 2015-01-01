package org.smoothbuild.db.objects;

import static org.smoothbuild.lang.type.Types.arrayElementJTypes;
import static org.smoothbuild.lang.type.Types.arrayTypeContaining;
import static org.smoothbuild.lang.type.Types.jTypeToType;

import javax.inject.Inject;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;
import org.smoothbuild.db.objects.marshal.ObjectMarshallers;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.BlobBuilder;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;

import com.google.common.hash.HashCode;
import com.google.inject.TypeLiteral;

public class ObjectsDb {
  private final ObjectMarshallers objectMarshallers;

  @Inject
  public ObjectsDb(ObjectMarshallers objectMarshallers) {
    this.objectMarshallers = objectMarshallers;
  }

  public <T extends Value> ArrayBuilder<T> arrayBuilder(Class<T> elementClass) {
    if (!(arrayElementJTypes().contains(TypeLiteral.get(elementClass)))) {
      throw new IllegalArgumentException("Illegal type " + elementClass.getCanonicalName());
    }
    Type type = jTypeToType(TypeLiteral.get(elementClass));
    return createArrayBuilder(arrayTypeContaining(type), elementClass);
  }

  private <T extends Value> ArrayBuilder<T> createArrayBuilder(ArrayType type, Class<?> elementClass) {
    ArrayMarshaller<T> marshaller = objectMarshallers.arrayMarshaller(type);
    return new ArrayBuilder<>(marshaller, elementClass);
  }

  public SFile file(Path path, Blob content) {
    return objectMarshallers.fileMarshaller().write(path, content);
  }

  public BlobBuilder blobBuilder() {
    return new BlobBuilder(objectMarshallers.blobMarshaller());
  }

  public SString string(String string) {
    return objectMarshallers.stringMarshaller().write(string);
  }

  public Value read(Type type, HashCode hash) {
    return objectMarshallers.marshaller(type).read(hash);
  }
}
