package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.BlobTypeH;
import org.smoothbuild.db.object.type.val.IntTypeH;
import org.smoothbuild.db.object.type.val.StringTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.BlobTypeS;
import org.smoothbuild.lang.base.type.impl.BoolTypeS;
import org.smoothbuild.lang.base.type.impl.FunctionTypeS;
import org.smoothbuild.lang.base.type.impl.IntTypeS;
import org.smoothbuild.lang.base.type.impl.NothingTypeS;
import org.smoothbuild.lang.base.type.impl.StringTypeS;
import org.smoothbuild.lang.base.type.impl.StructTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.lang.base.type.impl.VariableS;

public class TypeSToTypeOConverter {
  private final ObjFactory objFactory;

  @Inject
  public TypeSToTypeOConverter(ObjFactory objFactory) {
    this.objFactory = objFactory;
  }

  public TypeHV visit(TypeS type) {
    // TODO refactor to pattern matching once we have java 17
    if (type instanceof BlobTypeS blob) {
      return visit(blob);
    } else if (type instanceof BoolTypeS) {
      return objFactory.boolType();
    } else if (type instanceof IntTypeS intType) {
      return visit(intType);
    } else if (type instanceof NothingTypeS) {
      return objFactory.nothingType();
    } else if (type instanceof StringTypeS stringType) {
      return visit(stringType);
    } else if (type instanceof StructTypeS structType) {
      return visit(structType);
    } else if (type instanceof VariableS) {
      throw new UnsupportedOperationException();
    } else if (type instanceof ArrayTypeS array) {
      return visit(array);
    } else if (type instanceof FunctionTypeS) {
      return nativeCodeType();
    } else {
      throw new IllegalArgumentException("Unknown type " + type.getClass().getCanonicalName());
    }
  }

  public BlobTypeH visit(BlobTypeS type) {
    return objFactory.blobType();
  }

  public IntTypeH visit(IntTypeS type) {
    return objFactory.intType();
  }

  public StringTypeH visit(StringTypeS string) {
    return objFactory.stringType();
  }

  public TupleTypeH visit(StructTypeS structType) {
    var itemTypes = map(structType.fields(), isig -> visit(isig.type()));
    return objFactory.tupleType(itemTypes);
  }

  public ArrayTypeH visit(ArrayTypeS array) {
    if (array.isPolytype()) {
      throw new UnsupportedOperationException();
    }
    return objFactory.arrayType(visit(array.element()));
  }

  private TupleTypeH nativeCodeType() {
    return objFactory.tupleType(
        list(objFactory.stringType(), objFactory.blobType()));
  }

  public TupleTypeH functionType() {
    return objFactory.tupleType(list(objFactory.stringType(), objFactory.blobType()));
  }
}
