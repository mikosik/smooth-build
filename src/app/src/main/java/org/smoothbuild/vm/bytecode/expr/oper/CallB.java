package org.smoothbuild.vm.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.vm.bytecode.type.Validator.validateArgs;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.BytecodeDbException;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.vm.bytecode.type.oper.CallCB;
import org.smoothbuild.vm.bytecode.type.value.FuncTB;
import org.smoothbuild.vm.bytecode.type.value.TupleTB;

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
  public SubExprsB subExprs() throws BytecodeException {
    var func = readFunc();
    var args = readArgs();
    validate(func, args);
    return new SubExprsB(func, args);
  }

  private void validate(ExprB func, CombineB args) throws BytecodeDbException {
    if (func.evaluationT() instanceof FuncTB funcTB) {
      validate(funcTB, args);
    } else {
      throw new DecodeExprWrongNodeTypeException(
          hash(), this.category(), "func", FuncTB.class, func.evaluationT());
    }
  }

  private void validate(FuncTB funcTB, CombineB args) throws BytecodeDbException {
    var argsT = args.evaluationT();
    validateArgs(funcTB, argsT.elements(), () -> illegalArgsExc(funcTB.params(), argsT));
    var resultT = funcTB.result();
    if (!evaluationT().equals(resultT)) {
      throw new DecodeExprWrongNodeTypeException(
          hash(), this.category(), "call.result", evaluationT(), resultT);
    }
  }

  private BytecodeDbException illegalArgsExc(TupleTB params, TupleTB argsType) {
    return new DecodeExprWrongNodeTypeException(hash(), this.category(), "args", params, argsType);
  }

  private ExprB readFunc() throws BytecodeException {
    return readDataSeqElem(CALLABLE_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private CombineB readArgs() throws BytecodeException {
    return readDataSeqElem(ARGS_IDX, DATA_SEQ_SIZE, CombineB.class);
  }

  public static record SubExprsB(ExprB func, CombineB args) implements ExprsB {
    @Override
    public List<ExprB> toList() {
      return list(func, args());
    }
  }
}
