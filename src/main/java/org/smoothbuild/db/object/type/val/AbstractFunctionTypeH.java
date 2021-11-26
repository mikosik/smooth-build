package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.TypeKindH.ABSTRACT_FUNCTION;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeHV;

public class AbstractFunctionTypeH extends FunctionTypeH {
  public AbstractFunctionTypeH(Hash hash, TypeHV result, TupleTypeH paramsTuple) {
    super(hash, ABSTRACT_FUNCTION, result, paramsTuple);
  }
}
