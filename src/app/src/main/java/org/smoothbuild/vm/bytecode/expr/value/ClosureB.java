package org.smoothbuild.vm.bytecode.expr.value;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.MerkleRoot;
import org.smoothbuild.vm.bytecode.expr.exc.DecodeExprWrongNodeTypeExc;
import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.type.value.ClosureCB;

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
    var evaluationT = type();
    var funcT = func.type();
    if (!evaluationT.equals(funcT)) {
      throw new DecodeExprWrongNodeTypeExc(hash(), category(), DATA_PATH, evaluationT, funcT);
    }
    return func;
  }

  @Override
  public String exprToString() {
    return "Closure(" + type().name() + ")";
  }
}
