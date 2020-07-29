package org.smoothbuild.exec.plan;

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
import org.smoothbuild.record.db.RecordFactory;
import org.smoothbuild.record.spec.ArraySpec;
import org.smoothbuild.record.spec.BlobSpec;
import org.smoothbuild.record.spec.BoolSpec;
import org.smoothbuild.record.spec.NothingSpec;
import org.smoothbuild.record.spec.Spec;
import org.smoothbuild.record.spec.StringSpec;
import org.smoothbuild.record.spec.TupleSpec;

public class TypeToBinaryTypeConverter extends TypeVisitor<Spec> {
  private final RecordFactory recordFactory;

  public TypeToBinaryTypeConverter(RecordFactory recordFactory) {
    this.recordFactory = recordFactory;
  }

  @Override
  public BlobSpec visit(BlobType type) {
    return recordFactory.blobSpec();
  }

  @Override
  public BoolSpec visit(BoolType type) {
    return recordFactory.boolSpec();
  }

  @Override
  public NothingSpec visit(NothingType type) {
    return recordFactory.nothingSpec();
  }

  @Override
  public StringSpec visit(StringType type) {
    return recordFactory.stringSpec();
  }

  @Override
  public TupleSpec visit(StructType type) {
    Iterable<Spec> fieldTypes =
        type.fields().values().stream()
            .map(f -> f.type().visit(this))
            .collect(toImmutableList());
    return recordFactory.tupleSpec(fieldTypes);
  }

  @Override
  public Spec visit(GenericType type) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ArraySpec visit(ConcreteArrayType type) {
    return recordFactory.arraySpec(type.elemType().visit(this));
  }

  @Override
  public Spec visit(GenericArrayType type) {
    throw new UnsupportedOperationException();
  }
}
