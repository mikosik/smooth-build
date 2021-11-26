package org.smoothbuild.exec.plan;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.db.object.type.val.BlobTypeH;
import org.smoothbuild.db.object.type.val.FunctionTypeH;
import org.smoothbuild.db.object.type.val.IntTypeH;
import org.smoothbuild.db.object.type.val.StringTypeH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.db.object.type.val.VariableH;
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

public class TypeShConverter {
  private final ObjFactory objFactory;

  @Inject
  public TypeShConverter(ObjFactory objFactory) {
    this.objFactory = objFactory;
  }

  public TypeHV visit(TypeS type) {
    return switch (type) {
      case BlobTypeS blobS -> visit(blobS);
      case BoolTypeS boolS -> objFactory.boolType();
      case IntTypeS intS -> visit(intS);
      case NothingTypeS nothingS -> objFactory.nothingType();
      case StringTypeS stringS -> visit(stringS);
      case StructTypeS structS -> visit(structS);
      case VariableS variableS ->  visit(variableS);
      case ArrayTypeS arrayS -> visit(arrayS);
      case FunctionTypeS functionS -> visit(functionS);
      default -> throw new IllegalArgumentException("Unknown type " + type.getClass().getCanonicalName());
    };
  }

  public BlobTypeH visit(BlobTypeS type) {
    return objFactory.blobType();
  }

  public IntTypeH visit(IntTypeS type) {
    return objFactory.intType();
  }

  public FunctionTypeH visit(FunctionTypeS type) {
    return objFactory.definedFunctionType(visit(type.result()), map(type.parameters(), this::visit));
  }

  public StringTypeH visit(StringTypeS string) {
    return objFactory.stringType();
  }

  public TupleTypeH visit(StructTypeS structType) {
    var itemTypes = map(structType.fields(), isig -> visit(isig.type()));
    return objFactory.tupleType(itemTypes);
  }

  public VariableH visit(VariableS variable) {
    return objFactory.variable(variable.name());
  }

  public ArrayTypeH visit(ArrayTypeS array) {
    return objFactory.arrayType(visit(array.element()));
  }

  public TupleTypeH functionType() {
    return objFactory.tupleType(list(objFactory.stringType(), objFactory.blobType()));
  }
}
