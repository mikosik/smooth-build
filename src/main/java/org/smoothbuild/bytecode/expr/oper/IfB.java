package org.smoothbuild.bytecode.expr.oper;

import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.MerkleRoot;

/**
 * If expression.
 *
 * This class is thread-safe.
 */
public final class IfB extends OperB {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int COND_IDX = 0;
  private static final int THEN_IDX = 1;
  private static final int ELSE_IDX = 2;

  public IfB(MerkleRoot merkleRoot, BytecodeDb bytecodeDb) {
    super(merkleRoot, bytecodeDb);
  }

  public Data data() {
    return new Data(readCondition(), readThen(), readElse());
  }

  public record Data(ExprB condition, ExprB then, ExprB else_) {}

  private ExprB readCondition() {
    var expectedT = bytecodeDb().catDb().bool();
    return readSeqElemExprWithType(DATA_PATH, dataHash(), COND_IDX, DATA_SEQ_SIZE, expectedT);
  }

  private ExprB readThen() {
    return readSeqElemExprWithType(DATA_PATH, dataHash(), THEN_IDX, DATA_SEQ_SIZE, type());
  }

  private ExprB readElse() {
    return readSeqElemExprWithType(DATA_PATH, dataHash(), ELSE_IDX, DATA_SEQ_SIZE, type());
  }
}
