package org.smoothbuild.db.object.obj.expr;

import java.util.Objects;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.object.type.val.CallableTH;
import org.smoothbuild.db.object.type.val.TupleTH;

public class CallLikeH extends ExprH {
  protected static final int DATA_SEQ_SIZE = 2;
  protected static final int CALLABLE_INDEX = 0;
  protected static final int ARGS_INDEX = 1;

  public CallLikeH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  protected void validate(CallableTH callableT, CombineH argsCombine) {
    var argsT = argsCombine.type();
    var actualResT = objDb().inferCallResT(callableT, argsT, () -> illegalArgs(callableT, argsT));
    if (!Objects.equals(type(), actualResT)) {
      throw new DecodeObjWrongNodeTypeExc(hash(), cat(), "callable.result", type(), actualResT);
    }
  }

  private void illegalArgs(CallableTH callableTH, TupleTH argsType) {
    throw new DecodeObjWrongNodeTypeExc(
        hash(), this.cat(), "args", callableTH.paramsTuple(), argsType);
  }
}
