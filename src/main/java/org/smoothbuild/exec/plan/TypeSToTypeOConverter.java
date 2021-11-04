package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Named.named;
import static org.smoothbuild.util.collect.NamedList.namedList;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.db.object.type.val.ArrayOType;
import org.smoothbuild.db.object.type.val.BlobOType;
import org.smoothbuild.db.object.type.val.IntOType;
import org.smoothbuild.db.object.type.val.StringOType;
import org.smoothbuild.db.object.type.val.StructOType;
import org.smoothbuild.db.object.type.val.TupleOType;
import org.smoothbuild.lang.base.type.api.ArrayType;
import org.smoothbuild.lang.base.type.api.BlobType;
import org.smoothbuild.lang.base.type.api.BoolType;
import org.smoothbuild.lang.base.type.api.FunctionType;
import org.smoothbuild.lang.base.type.api.IntType;
import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.StringType;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Variable;

public class TypeSToTypeOConverter {
  private final ObjectFactory objectFactory;

  @Inject
  public TypeSToTypeOConverter(ObjectFactory objectFactory) {
    this.objectFactory = objectFactory;
  }

  public ValType visit(Type type) {
    // TODO refactor to pattern matching once we have java 17
    if (type instanceof BlobType blob) {
      return visit(blob);
    } else if (type instanceof BoolType) {
      return objectFactory.boolType();
    } else if (type instanceof IntType intType) {
      return visit(intType);
    } else if (type instanceof NothingType) {
      return objectFactory.nothingType();
    } else if (type instanceof StringType stringType) {
      return visit(stringType);
    } else if (type instanceof StructType structType) {
      var fields = structType.fields().mapObjects(this::visit);
      return objectFactory.structType(structType.name(), fields);
    } else if (type instanceof Variable) {
      throw new UnsupportedOperationException();
    } else if (type instanceof ArrayType array) {
      return visit(array);
    } else if (type instanceof FunctionType) {
      return nativeCodeType();
    } else {
      throw new IllegalArgumentException("Unknown type " + type.getClass().getCanonicalName());
    }
  }

  public BlobOType visit(BlobType type) {
    return objectFactory.blobType();
  }

  public IntOType visit(IntType type) {
    return objectFactory.intType();
  }

  public StringOType visit(StringType string) {
    return objectFactory.stringType();
  }

  public ArrayOType visit(ArrayType array) {
    if (array.isPolytype()) {
      throw new UnsupportedOperationException();
    }
    return objectFactory.arrayType(visit(array.element()));
  }

  private TupleOType nativeCodeType() {
    return objectFactory.tupleType(
        list(objectFactory.stringType(), objectFactory.blobType()));
  }

  public StructOType functionType() {
    return objectFactory.structType(
        "", namedList(list(named(objectFactory.stringType()), named(objectFactory.blobType()))));
  }
}
