package org.smoothbuild.bytecode.obj.expr;

import static org.smoothbuild.bytecode.type.IsAssignable.isAssignable;
import static org.smoothbuild.bytecode.type.IsAssignable.validateArgs;

import org.smoothbuild.bytecode.obj.ObjDbImpl;
import org.smoothbuild.bytecode.obj.base.ExprB;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.exc.DecodeObjWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.cnst.CallableTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;

public class CallableB extends ExprB {
  protected static final int DATA_SEQ_SIZE = 2;
  protected static final int CALLABLE_IDX = 0;
  protected static final int ARGS_IDX = 1;

  public CallableB(MerkleRoot merkleRoot, ObjDbImpl objDb) {
    super(merkleRoot, objDb);
  }

  protected void validate(CallableTB callableTB, CombineB argsCombine) {
    var resT = callableTB.res();
    validateArgs(callableTB, argsCombine.type().items(),
        () -> illegalArgsExc(callableTB, argsCombine.type()));
    if (!isAssignable(type(), resT)) {
      throw new DecodeObjWrongNodeTypeExc(hash(), cat(), "callable.result", type(), resT);
    }
  }

  private RuntimeException illegalArgsExc(CallableTB callableTB, TupleTB argsType) {
    return new DecodeObjWrongNodeTypeExc(
        hash(), this.cat(), "args", callableTB.paramsTuple(), argsType);
  }
}
