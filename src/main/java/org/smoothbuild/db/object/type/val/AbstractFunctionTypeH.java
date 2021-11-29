package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.ABSTRACT_FUNCTION;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;

public class AbstractFunctionTypeH extends FunctionTypeH {
  public AbstractFunctionTypeH(Hash hash, TypeH result, TupleTypeH paramsTuple) {
    super(hash, ABSTRACT_FUNCTION, result, paramsTuple);
  }
}
