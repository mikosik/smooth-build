package org.smoothbuild.db.objects;

import static org.smoothbuild.lang.base.Types.arrayTypeContaining;
import static org.smoothbuild.lang.base.Types.jTypeToType;

import javax.inject.Inject;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;
import org.smoothbuild.db.objects.marshal.ObjectMarshallers;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.Nothing;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.base.ValueFactory;

import com.google.common.hash.HashCode;
import com.google.inject.TypeLiteral;

public class ObjectsDb implements ValueFactory {
  private final ObjectMarshallers objectMarshallers;

  @Inject
  public ObjectsDb(ObjectMarshallers objectMarshallers) {
    this.objectMarshallers = objectMarshallers;
  }

  @Override
  public <T extends Value> ArrayBuilder<T> arrayBuilder(Class<T> elementClass) {
    if (!(elementClass == Nothing.class || elementClass == SString.class
        || elementClass == Blob.class || elementClass == SFile.class)) {
      throw new IllegalArgumentException("Illegal type " + elementClass.getCanonicalName());
    }
    Type type = jTypeToType(TypeLiteral.get(elementClass));
    return createArrayBuilder(arrayTypeContaining(type), elementClass);
  }

  private <T extends Value> ArrayBuilder<T> createArrayBuilder(ArrayType type, Class<?> elementClass) {
    ArrayMarshaller<T> marshaller = objectMarshallers.arrayMarshaller(type);
    return new ArrayBuilder<>(marshaller, elementClass);
  }

  @Override
  public SFile file(Path path, Blob content) {
    return objectMarshallers.fileMarshaller().write(path, content);
  }

  @Override
  public BlobBuilder blobBuilder() {
    return new BlobBuilder(objectMarshallers.blobMarshaller());
  }

  @Override
  public SString string(String string) {
    return objectMarshallers.stringMarshaller().write(string);
  }

  public Value read(Type type, HashCode hash) {
    return objectMarshallers.marshaller(type).read(hash);
  }
}
