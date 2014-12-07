package org.smoothbuild.db.objects.build;

import static org.smoothbuild.lang.base.Types.NIL;

import javax.inject.Inject;

import org.smoothbuild.db.objects.marshal.ArrayMarshaller;
import org.smoothbuild.db.objects.marshal.ObjectMarshallers;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.BlobBuilder;
import org.smoothbuild.lang.base.Value;

public class ObjectBuilders {
  private final ObjectMarshallers objectMarshallers;

  @Inject
  public ObjectBuilders(ObjectMarshallers objectMarshallers) {
    this.objectMarshallers = objectMarshallers;
  }

  public BlobBuilder blobBuilder() {
    return new BlobBuilderImpl(objectMarshallers.blobMarshaller());
  }

  public <T extends Value> ArrayBuilder<T> arrayBuilder(ArrayType<T> arrayType) {
    if (arrayType == NIL) {
      return (ArrayBuilder<T>) new NilBuilder(objectMarshallers.arrayMarshaller(NIL));
    } else {
      return createArrayBuilder(arrayType);
    }
  }

  private <T extends Value> ArrayBuilder<T> createArrayBuilder(ArrayType<T> arrayType) {
    ArrayMarshaller<T> marshaller = objectMarshallers.arrayMarshaller(arrayType);
    return new ArrayBuilderImpl<>(arrayType, marshaller);
  }
}
