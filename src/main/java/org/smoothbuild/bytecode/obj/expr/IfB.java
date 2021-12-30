package org.smoothbuild.bytecode.obj.expr;

import org.smoothbuild.bytecode.obj.ByteDbImpl;
import org.smoothbuild.bytecode.obj.base.ExprB;
import org.smoothbuild.bytecode.obj.base.MerkleRoot;
import org.smoothbuild.bytecode.obj.base.ObjB;

/**
 * If expression.
 *
 * This class is thread-safe.
 */
public final class IfB extends ExprB {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int COND_IDX = 0;
  private static final int THEN_IDX = 1;
  private static final int ELSE_IDX = 2;

  public IfB(MerkleRoot merkleRoot, ByteDbImpl byteDb) {
    super(merkleRoot, byteDb);
  }

  public Data data() {
    return new Data(readCondition(), readThen(), readElse());
  }

  public record Data(ObjB condition, ObjB then, ObjB else_) {}

  private ObjB readCondition() {
    var expectedT = byteDb().catDb().bool();
    return readSeqElemObjWithType(DATA_PATH, dataHash(), COND_IDX, DATA_SEQ_SIZE, expectedT);
  }

  private ObjB readThen() {
    return readSeqElemObjWithType(DATA_PATH, dataHash(), THEN_IDX, DATA_SEQ_SIZE, type());
  }

  private ObjB readElse() {
    return readSeqElemObjWithType(DATA_PATH, dataHash(), ELSE_IDX, DATA_SEQ_SIZE, type());
  }
}
