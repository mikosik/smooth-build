package org.smoothbuild.exec.task.plan;

import static com.google.common.collect.ImmutableList.toImmutableList;

import org.smoothbuild.lang.base.type.BlobType;
import org.smoothbuild.lang.base.type.BoolType;
import org.smoothbuild.lang.base.type.ConcreteArrayType;
import org.smoothbuild.lang.base.type.GenericArrayType;
import org.smoothbuild.lang.base.type.GenericType;
import org.smoothbuild.lang.base.type.NothingType;
import org.smoothbuild.lang.base.type.StringType;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.TypeVisitor;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.object.type.Field;

public class TypeToBinaryTypeConverter extends TypeVisitor<ConcreteType> {
  private final ObjectFactory objectFactory;

  public TypeToBinaryTypeConverter(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  public ConcreteType visit(BlobType type) {
    return objectFactory.blobType();
  }

  @Override
  public ConcreteType visit(BoolType type) {
    return objectFactory.boolType();
  }

  @Override
  public ConcreteType visit(NothingType type) {
    return objectFactory.nothingType();
  }

  @Override
  public ConcreteType visit(StringType type) {
    return objectFactory.stringType();
  }

  @Override
  public ConcreteType visit(StructType type) {
    Iterable<Field> binaryFields =
        type.fields().values().stream()
            .map(f -> new Field(f.type().visit(this), f.name(), null))
            .collect(toImmutableList());
    return objectFactory.structType(type.name(), binaryFields);
  }

  @Override
  public ConcreteType visit(GenericType type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ConcreteType visit(ConcreteArrayType type) {
    return objectFactory.arrayType(type.elemType().visit(this));
  }

  @Override
  public ConcreteType visit(GenericArrayType type) {
    throw new UnsupportedOperationException();
  }
}
