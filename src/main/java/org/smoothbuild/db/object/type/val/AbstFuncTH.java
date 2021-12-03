package org.smoothbuild.db.object.type.val;

import static org.smoothbuild.db.object.type.base.CatKindH.ABST_FUNC;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;

public class AbstFuncTH extends FuncTH {
  public AbstFuncTH(Hash hash, TypeH result, TupleTH paramsTuple) {
    super(hash, ABST_FUNC, result, paramsTuple);
  }
}
