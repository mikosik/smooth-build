package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.zip;
import static org.smoothbuild.util.collect.Named.named;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjectFactory;
import org.smoothbuild.db.object.spec.base.ValSpec;
import org.smoothbuild.db.object.spec.val.ArraySpec;
import org.smoothbuild.db.object.spec.val.BlobSpec;
import org.smoothbuild.db.object.spec.val.IntSpec;
import org.smoothbuild.db.object.spec.val.RecSpec;
import org.smoothbuild.db.object.spec.val.StrSpec;
import org.smoothbuild.db.object.spec.val.StructSpec;
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
    } else if (type instanceof StructType structType) {
      var types = map(structType.fields(), this::visit);
      var fields = zip(types, structType.names(), (t, n) -> named(n, t));
      return objectFactory.structSpec(structType.name(), fields);
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
    return objectFactory.arraySpec(visit(array.element()));
  }

  private RecSpec nativeCodeSpec() {
    return objectFactory.recSpec(
        list(objectFactory.stringSpec(), objectFactory.blobSpec()));
  }

  public StructSpec functionSpec() {
    return objectFactory.structSpec(
        "", list(named(objectFactory.stringSpec()), named(objectFactory.blobSpec())));
  }
}
