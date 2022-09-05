package org.smoothbuild.bytecode.expr.oper;

import static org.smoothbuild.bytecode.type.ValidateArgs.validateArgs;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.val.CallableTB;
import org.smoothbuild.bytecode.type.val.TupleTB;

public class CallableB extends OperB {
  protected static final int DATA_SEQ_SIZE = 2;
  protected static final int CALLABLE_IDX = 0;
  protected static final int ARGS_IDX = 1;

  public CallableB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  protected void validate(CallableTB callableTB, CombineB argsCombine) {
    var resT = callableTB.res();
    validateArgs(callableTB, argsCombine.type().items(),
        () -> illegalArgsExc(callableTB, argsCombine.type()));
    if (!type().equals(resT)) {
      throw new DecodeExprWrongNodeTypeExc(hash(), cat(), "callable.result", type(), resT);
    }
  }

  private RuntimeException illegalArgsExc(CallableTB callableTB, TupleTB argsType) {
    return new DecodeExprWrongNodeTypeExc(
        hash(), this.cat(), "args", callableTB.paramsTuple(), argsType);
  }
}
