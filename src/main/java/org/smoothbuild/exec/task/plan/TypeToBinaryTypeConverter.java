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
  public org.smoothbuild.lang.object.type.NothingType visit(NothingType type) {
    return objectFactory.nothingType();
  }

  @Override
  public org.smoothbuild.lang.object.type.StringType visit(StringType type) {
    return objectFactory.stringType();
  }

  @Override
  public org.smoothbuild.lang.object.type.StructType visit(StructType type) {
    Iterable<ConcreteType> fieldTypes =
        type.fields().values().stream()
            .map(f -> f.type().visit(this))
            .collect(toImmutableList());
    return objectFactory.structType(fieldTypes);
  }

  @Override
  public ConcreteType visit(GenericType type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public org.smoothbuild.lang.object.type.ConcreteArrayType visit(ConcreteArrayType type) {
    return objectFactory.arrayType(type.elemType().visit(this));
  }

  @Override
  public ConcreteType visit(GenericArrayType type) {
    throw new UnsupportedOperationException();
  }
}
