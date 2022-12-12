package org.smoothbuild.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.MerkleRoot;
import org.smoothbuild.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.bytecode.type.value.ClosureCB;

/**
 * Closure.
 * This class is thread-safe.
 */
public final class ClosureB extends FuncB {
  private static final int DATA_SEQ_SIZE = 2;
  private static final int ENVIRONMENT_IDX = 0;
  private static final int BODY_IDX = 1;

  public ClosureB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
    checkArgument(merkleRoot.category() instanceof ClosureCB);
  }

  public CombineB environment() {
    return readDataSeqElem(ENVIRONMENT_IDX, DATA_SEQ_SIZE, CombineB.class);
  }

  public ExprFuncB func() {
    var func = readDataSeqElem(BODY_IDX, DATA_SEQ_SIZE, ExprFuncB.class);
    var evalT = type();
    var funcT = func.type();
    if (!evalT.equals(funcT)) {
      throw new DecodeExprWrongNodeTypeExc(hash(), category(), DATA_PATH, evalT, funcT);
    }
    return func;
  }

  @Override
  public String exprToString() {
    return "Closure(" + type().name() + ")";
  }
}
