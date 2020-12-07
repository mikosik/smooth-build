package org.smoothbuild.exec.plan;

import static com.google.common.collect.ImmutableList.toImmutableList;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.spec.AnySpec;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.BlobSpec;
import org.smoothbuild.db.object.spec.BoolSpec;
import org.smoothbuild.db.object.spec.NothingSpec;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.StringSpec;
import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.lang.base.type.AnyType;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.BlobType;
import org.smoothbuild.lang.base.type.BoolType;
import org.smoothbuild.lang.base.type.NothingType;
import org.smoothbuild.lang.base.type.StringType;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.TypeVariable;
import org.smoothbuild.lang.base.type.TypeVisitor;

public class TypeToSpecConverter extends TypeVisitor<Spec> {
  private final ObjectFactory objectFactory;

  public TypeToSpecConverter(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  @Override
  public AnySpec visit(AnyType type) {
    return objectFactory.anySpec();
  }

  @Override
  public BlobSpec visit(BlobType type) {
    return objectFactory.blobSpec();
  }

  @Override
  public BoolSpec visit(BoolType type) {
    return objectFactory.boolSpec();
  }

  @Override
  public NothingSpec visit(NothingType type) {
    return objectFactory.nothingSpec();
  }

  @Override
  public StringSpec visit(StringType type) {
    return objectFactory.stringSpec();
  }

  @Override
  public TupleSpec visit(StructType type) {
    Iterable<Spec> fieldTypes =
        type.fields().stream()
            .map(f -> f.type().visit(this))
            .collect(toImmutableList());
    return objectFactory.tupleSpec(fieldTypes);
  }

  @Override
  public Spec visit(TypeVariable type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ArraySpec visit(ArrayType type) {
    if (type.isPolytype()) {
      throw new UnsupportedOperationException();
    }
    return objectFactory.arraySpec(type.elemType().visit(this));
  }
}
