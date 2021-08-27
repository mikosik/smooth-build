package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.BlobSpec;
import org.smoothbuild.db.object.spec.IntSpec;
import org.smoothbuild.db.object.spec.StrSpec;
import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.db.object.spec.ValSpec;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.BlobType;
import org.smoothbuild.lang.base.type.BoolType;
import org.smoothbuild.lang.base.type.FunctionType;
import org.smoothbuild.lang.base.type.IntType;
import org.smoothbuild.lang.base.type.NothingType;
import org.smoothbuild.lang.base.type.StringType;
import org.smoothbuild.lang.base.type.StructType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Variable;

public class TypeToSpecConverter {
  private final ObjectFactory objectFactory;

  @Inject
  public TypeToSpecConverter(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  public ValSpec visit(Type type) {
    // TODO refactor to pattern matching once we have java 17
    if (type instanceof BlobType blob) {
      return visit(blob);
    } else if (type instanceof BoolType) {
      return objectFactory.boolSpec();
    } else if (type instanceof IntType intType) {
      return visit(intType);
    } else if (type instanceof NothingType) {
      return objectFactory.nothingSpec();
    } else if (type instanceof StringType stringType) {
      return visit(stringType);
    } else if (type instanceof StructType struct) {
      Iterable<ValSpec> fieldSpecs = map(struct.fields(), f -> visit(f.type()));
      return objectFactory.tupleSpec(fieldSpecs);
    } else if (type instanceof Variable) {
      throw new UnsupportedOperationException();
    } else if (type instanceof ArrayType array) {
      return visit(array);
    } else if (type instanceof FunctionType) {
      return nativeCodeSpec();
    } else {
      throw new IllegalArgumentException("Unknown type " + type.getClass().getCanonicalName());
    }
  }

  public BlobSpec visit(BlobType type) {
    return objectFactory.blobSpec();
  }

  public IntSpec visit(IntType type) {
    return objectFactory.intSpec();
  }

  public StrSpec visit(StringType string) {
    return objectFactory.stringSpec();
  }

  public ArraySpec visit(ArrayType array) {
    if (array.isPolytype()) {
      throw new UnsupportedOperationException();
    }
    return objectFactory.arraySpec(visit(array.elemType()));
  }

  private TupleSpec nativeCodeSpec() {
    return objectFactory.tupleSpec(
        list(objectFactory.stringSpec(), objectFactory.blobSpec()));
  }

  public TupleSpec functionSpec() {
    return objectFactory.tupleSpec(
        list(objectFactory.stringSpec(), objectFactory.blobSpec()));
  }
}
