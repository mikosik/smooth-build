package org.smoothbuild.db.objects.build;

import static org.smoothbuild.lang.base.STypes.BLOB_ARRAY;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;

import javax.inject.Inject;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;
import org.smoothbuild.db.objects.marshal.ObjectMarshallers;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SValue;

public class ObjectBuilders {
  private final ObjectMarshallers objectMarshallers;

  @Inject
  public ObjectBuilders(ObjectMarshallers objectMarshallers) {
    this.objectMarshallers = objectMarshallers;
  }

  public BlobBuilder blobBuilder() {
    return new BlobBuilderImpl(objectMarshallers.blobMarshaller());
  }

  public <T extends SValue> ArrayBuilder<T> arrayBuilder(SArrayType<T> arrayType) {
    /*
     * Each cast is safe as it is preceded by checking arrayType.
     */
    if (arrayType == FILE_ARRAY) {
      return cast(createArrayBuilder(FILE_ARRAY));
    }
    if (arrayType == BLOB_ARRAY) {
      return cast(createArrayBuilder(BLOB_ARRAY));
    }
    if (arrayType == STRING_ARRAY) {
      return cast(createArrayBuilder(STRING_ARRAY));
    }
    if (arrayType == NIL) {
      return cast(new NilBuilder(objectMarshallers.arrayMarshaller(NIL)));
    }

    throw new IllegalArgumentException("Cannot create ArrayWriter for array type = " + arrayType);
  }

  @SuppressWarnings("unchecked")
  private static <T extends SValue> ArrayBuilder<T> cast(ArrayBuilder<?> arrayWriter) {
    return (ArrayBuilder<T>) arrayWriter;
  }

  private <T extends SValue> ArrayBuilder<T> createArrayBuilder(SArrayType<T> arrayType) {
    ArrayMarshaller<T> marshaller = objectMarshallers.arrayMarshaller(arrayType);
    return new ArrayBuilderImpl<>(arrayType, marshaller);
  }
}
