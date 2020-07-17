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
import org.smoothbuild.record.db.ObjectFactory;
import org.smoothbuild.record.type.ArrayType;
import org.smoothbuild.record.type.BinaryType;
import org.smoothbuild.record.type.TupleType;

public class TypeToBinaryTypeConverter extends TypeVisitor<BinaryType> {
  private final ObjectFactory objectFactory;

  public TypeToBinaryTypeConverter(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  public BinaryType visit(BlobType type) {
    return objectFactory.blobType();
  }

  @Override
  public BinaryType visit(BoolType type) {
    return objectFactory.boolType();
  }

  @Override
  public org.smoothbuild.record.type.NothingType visit(NothingType type) {
    return objectFactory.nothingType();
  }

  @Override
  public org.smoothbuild.record.type.StringType visit(StringType type) {
    return objectFactory.stringType();
  }

  @Override
  public TupleType visit(StructType type) {
    Iterable<BinaryType> fieldTypes =
        type.fields().values().stream()
            .map(f -> f.type().visit(this))
            .collect(toImmutableList());
    return objectFactory.structType(fieldTypes);
  }

  @Override
  public BinaryType visit(GenericType type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ArrayType visit(ConcreteArrayType type) {
    return objectFactory.arrayType(type.elemType().visit(this));
  }

  @Override
  public BinaryType visit(GenericArrayType type) {
    throw new UnsupportedOperationException();
  }
}
