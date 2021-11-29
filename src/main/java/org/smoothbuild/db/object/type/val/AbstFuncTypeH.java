package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.SpecKindH.ABST_FUNC;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;

public class AbstFuncTypeH extends FuncTypeH {
  public AbstFuncTypeH(Hash hash, TypeH result, TupleTypeH paramsTuple) {
    super(hash, ABST_FUNC, result, paramsTuple);
  }
}
