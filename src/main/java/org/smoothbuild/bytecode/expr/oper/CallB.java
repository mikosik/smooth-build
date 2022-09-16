package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeClassExc;
import org.smoothbuild.bytecode.type.oper.CallCB;
import org.smoothbuild.bytecode.type.val.FuncCB;
import org.smoothbuild.bytecode.type.val.FuncTB;

/**
 * This class is thread-safe.
 */
public class CallB extends CallLikeB {
  public CallB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.cat() instanceof CallCB);
  }

  @Override
  public CallCB cat() {
    return (CallCB) super.cat();
  }

  public Data data() {
    var func = readFunc();
    var args = readArgs();
    validate(func, args);
    return new Data(func, args);
  }

  public record Data(ExprB callable, CombineB args) {}

  private void validate(ExprB func, CombineB argsCombine) {
    if (func.type() instanceof FuncTB funcT) {
      validate(funcT, argsCombine);
    } else {
      throw new DecodeExprWrongNodeClassExc(
          hash(), cat(), "func", FuncCB.class, func.type().getClass());
    }
  }

  private ExprB readFunc() {
    return readSeqElemExpr(DATA_PATH, dataHash(), CALLABLE_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private CombineB readArgs() {
    return readSeqElemExpr(DATA_PATH, dataHash(), ARGS_IDX, DATA_SEQ_SIZE, CombineB.class);
  }
}
