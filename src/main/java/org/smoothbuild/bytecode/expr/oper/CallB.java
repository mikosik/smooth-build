package org.smoothbuild.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.bytecode.type.Validator.validateArgs;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.type.oper.CallCB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;

/**
 * This class is thread-safe.
 */
public class CallB extends OperB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int CALLABLE_IDX = 0;
  private static final int ARGS_IDX = 1;

  public CallB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof CallCB);
  }

  @Override
  public CallCB category() {
    return (CallCB) super.category();
  }

  public Data data() {
    var func = readFunc();
    var args = readArgs();
    validate(func, args);
    return new Data(func, args);
  }

  public record Data(ExprB callable, CombineB args) {}

  private void validate(ExprB func, CombineB argsCombine) {
    if (func.type() instanceof FuncTB funcTB) {
      validate(funcTB, argsCombine);
    } else {
      throw new DecodeExprWrongNodeTypeExc(hash(), this.category(), "func", FuncTB.class, func.type());
    }
  }

  protected void validate(FuncTB funcTB, CombineB argsCombine) {
    var argsT = argsCombine.type();
    validateArgs(funcTB, argsT.items(), () -> illegalArgsExc(funcTB.params(), argsT));
    var resT = funcTB.res();
    if (!type().equals(resT)) {
      throw new DecodeExprWrongNodeTypeExc(hash(), this.category(), "call.result", type(), resT);
    }
  }

  private RuntimeException illegalArgsExc(TupleTB params, TupleTB argsType) {
    return new DecodeExprWrongNodeTypeExc(hash(), this.category(), "args", params, argsType);
  }

  private ExprB readFunc() {
    return readDataSeqElem(CALLABLE_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private CombineB readArgs() {
    return readDataSeqElem(ARGS_IDX, DATA_SEQ_SIZE, CombineB.class);
  }
}
