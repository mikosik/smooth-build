package org.smoothbuild.virtualmachine.bytecode.expr.oper;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.virtualmachine.bytecode.type.Validator.validateArgs;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.ExprDbException;
import org.smoothbuild.virtualmachine.bytecode.type.oper.CallCB;
import org.smoothbuild.virtualmachine.bytecode.type.value.FuncTB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TupleTB;

/**
 * This class is thread-safe.
 */
public class CallB extends OperB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int CALLABLE_IDX = 0;
  private static final int ARGS_IDX = 1;

  public CallB(MerkleRoot merkleRoot, ExprDb exprDb) {
    super(merkleRoot, exprDb);
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

  private void validate(ExprB func, CombineB args) throws ExprDbException {
    if (func.evaluationType() instanceof FuncTB funcTB) {
      validate(funcTB, args);
    } else {
      throw new DecodeExprWrongNodeTypeException(
          hash(), this.category(), "func", FuncTB.class, func.evaluationType());
    }
  }

  private void validate(FuncTB funcTB, CombineB args) throws ExprDbException {
    var argsT = args.evaluationType();
    validateArgs(funcTB, argsT.elements(), () -> illegalArgsExc(funcTB.params(), argsT));
    var resultT = funcTB.result();
    if (!evaluationType().equals(resultT)) {
      throw new DecodeExprWrongNodeTypeException(
          hash(), this.category(), "call.result", evaluationType(), resultT);
    }
  }

  private ExprDbException illegalArgsExc(TupleTB params, TupleTB argsType) {
    return new DecodeExprWrongNodeTypeException(hash(), this.category(), "args", params, argsType);
  }

  private ExprB readFunc() throws BytecodeException {
    return readElementFromDataAsInstanceChain(CALLABLE_IDX, DATA_SEQ_SIZE, ExprB.class);
  }

  private CombineB readArgs() throws BytecodeException {
    return readElementFromDataAsInstanceChain(ARGS_IDX, DATA_SEQ_SIZE, CombineB.class);
  }

  public static record SubExprsB(ExprB func, CombineB args) implements ExprsB {
    @Override
    public List<ExprB> toList() {
      return list(func, args());
    }
  }
}
