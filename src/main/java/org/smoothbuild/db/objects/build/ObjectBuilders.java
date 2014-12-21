package org.smoothbuild.db.objects.build;

import static org.smoothbuild.lang.base.Types.arrayTypeContaining;
import static org.smoothbuild.lang.base.Types.jTypeToType;

import javax.inject.Inject;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;
import org.smoothbuild.db.objects.marshal.ObjectMarshallers;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.Blob;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.Nothing;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;

import com.google.inject.TypeLiteral;

public class ObjectBuilders {
  private final ObjectMarshallers objectMarshallers;

  @Inject
  public ObjectBuilders(ObjectMarshallers objectMarshallers) {
    this.objectMarshallers = objectMarshallers;
  }

  public BlobBuilder blobBuilder() {
    return new BlobBuilder(objectMarshallers.blobMarshaller());
  }

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
}
