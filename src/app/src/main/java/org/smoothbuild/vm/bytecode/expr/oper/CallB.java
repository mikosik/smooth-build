package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.bytecode.type.Validator.validateArgs;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.vm.bytecode.type.oper.CallCB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;

import com.google.common.collect.ImmutableList;

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

  @Override
  public ImmutableList<ExprB> dataSeq() {
    var func = readFunc();
    var args = readArgs();
    validate(func, args);
    return list(func, args);
  }

  private void validate(ExprB func, CombineB argsCombine) {
    if (func.evaluationT() instanceof FuncTB funcTB) {
      validate(funcTB, argsCombine);
    } else {
      throw new DecodeExprWrongNodeTypeExc(
          hash(), this.category(), "func", FuncTB.class, func.evaluationT());
    }
  }

  protected void validate(FuncTB funcTB, CombineB argsCombine) {
    var argsT = argsCombine.evaluationT();
    validateArgs(funcTB, argsT.elements(), () -> illegalArgsExc(funcTB.params(), argsT));
    var resultT = funcTB.result();
    if (!evaluationT().equals(resultT)) {
      throw new DecodeExprWrongNodeTypeExc(
          hash(), this.category(), "call.result", evaluationT(), resultT);
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
