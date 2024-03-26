package org.smoothbuild.virtualmachine.bytecode.expr.base;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.virtualmachine.bytecode.type.base.Validator.validateArgs;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.MerkleRoot;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.BExprDbException;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.DecodeExprWrongNodeTypeException;
import org.smoothbuild.virtualmachine.bytecode.type.base.BCallKind;
import org.smoothbuild.virtualmachine.bytecode.type.base.BFuncType;
import org.smoothbuild.virtualmachine.bytecode.type.base.BTupleType;

/**
 * This class is thread-safe.
 */
public final class BCall extends BOper {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int CALLABLE_IDX = 0;
  private static final int ARGS_IDX = 1;

  public BCall(MerkleRoot merkleRoot, BExprDb exprDb) {
    super(merkleRoot, exprDb);
    checkArgument(merkleRoot.kind() instanceof BCallKind);
  }

  @Override
  public BCallKind kind() {
    return (BCallKind) super.kind();
  }

  @Override
  public SubExprsB subExprs() throws BytecodeException {
    var func = readFunc();
    var args = readArgs();
    validate(func, args);
    return new SubExprsB(func, args);
  }

  private void validate(BExpr func, BCombine args) throws BExprDbException {
    if (func.evaluationType() instanceof BFuncType funcType) {
      validate(funcType, args);
    } else {
      throw new DecodeExprWrongNodeTypeException(
          hash(), this.kind(), "func", BFuncType.class, func.evaluationType());
    }
  }

  private void validate(BFuncType funcType, BCombine args) throws BExprDbException {
    var argsT = args.evaluationType();
    validateArgs(funcType, argsT.elements(), () -> illegalArgsExc(funcType.params(), argsT));
    var resultT = funcType.result();
    if (!evaluationType().equals(resultT)) {
      throw new DecodeExprWrongNodeTypeException(
          hash(), this.kind(), "call.result", evaluationType(), resultT);
    }
  }

  private BExprDbException illegalArgsExc(BTupleType params, BTupleType argsType) {
    return new DecodeExprWrongNodeTypeException(hash(), this.kind(), "args", params, argsType);
  }

  private BExpr readFunc() throws BytecodeException {
    return readElementFromDataAsInstanceChain(CALLABLE_IDX, DATA_SEQ_SIZE, BExpr.class);
  }

  private BCombine readArgs() throws BytecodeException {
    return readElementFromDataAsInstanceChain(ARGS_IDX, DATA_SEQ_SIZE, BCombine.class);
  }

  public static record SubExprsB(BExpr func, BCombine args) implements BExprs {
    @Override
    public List<BExpr> toList() {
      return list(func, args());
    }
  }
}
