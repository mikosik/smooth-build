package org.smoothbuild.db.bytecode.obj.expr;

import org.smoothbuild.db.bytecode.obj.ByteDb;
import org.smoothbuild.db.bytecode.obj.base.ExprB;
import org.smoothbuild.db.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.db.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.db.bytecode.type.val.CallableTB;
import org.smoothbuild.db.bytecode.type.val.TupleTB;

public class CallLikeB extends ExprB {
  protected static final int DATA_SEQ_SIZE = 2;
  protected static final int CALLABLE_IDX = 0;
  protected static final int ARGS_IDX = 1;

  public CallLikeB(MerkleRoot merkleRoot, ByteDb byteDb) {
    super(merkleRoot, byteDb);
  }

  protected void validate(CallableTB callableT, CombineB argsCombine) {
    var argsT = argsCombine.type();
    var actualResT = byteDb().typing()
        .inferCallResT(callableT, argsT.items(), () -> illegalArgsExc(callableT, argsT));
    if (!byteDb().typing().isAssignable(type(), actualResT)) {
      throw new DecodeObjWrongNodeTypeExc(hash(), cat(), "callable.result", type(), actualResT);
    }
  }

  private RuntimeException illegalArgsExc(CallableTB callableTB, TupleTB argsType) {
    return new DecodeObjWrongNodeTypeExc(
        hash(), this.cat(), "args", callableTB.paramsTuple(), argsType);
  }
}
