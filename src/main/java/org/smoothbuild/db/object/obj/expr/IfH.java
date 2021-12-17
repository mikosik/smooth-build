package org.smoothbuild.db.object.obj.expr;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.ExprH;
import org.smoothbuild.db.object.obj.base.MerkleRoot;
import org.smoothbuild.db.object.obj.base.ObjH;

/**
 * If expression.
 *
 * This class is thread-safe.
 */
public final class IfH extends ExprH {
  private static final int DATA_SEQ_SIZE = 3;
  private static final int COND_INDEX = 0;
  private static final int THEN_INDEX = 1;
  private static final int ELSE_INDEX = 2;

  public IfH(MerkleRoot merkleRoot, ObjDb objDb) {
    super(merkleRoot, objDb);
  }

  public IfData data() {
    return new IfData(readCondition(), readThen(), readElse());
  }

  public record IfData(ObjH condition, ObjH then_, ObjH else_) {}

  private ObjH readCondition() {
    var expectedT = objDb().catDb().bool();
    return readSeqElemObjWithType(DATA_PATH, dataHash(), COND_INDEX, DATA_SEQ_SIZE, expectedT);
  }

  private ObjH readThen() {
    return readSeqElemObjWithType(DATA_PATH, dataHash(), THEN_INDEX, DATA_SEQ_SIZE, type());
  }

  private ObjH readElse() {
    return readSeqElemObjWithType(DATA_PATH, dataHash(), ELSE_INDEX, DATA_SEQ_SIZE, type());
  }
}
